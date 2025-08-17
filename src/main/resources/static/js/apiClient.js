//Axios를 CDN에서 ESM으로 불러옴.
import axios from "https://cdn.jsdelivr.net/npm/axios@1.6.7/+esm";

const defaultPath = "/api";

// 사용할 API baseURL을 결정
function resolveBaseURL() {
  let base =
      window.__ENV?.API_BASE_URL ||
      document.querySelector('meta[name="api-base-url"]')?.content ||
      (location.origin + defaultPath); // 필요하면 "/api" 같은 기본 경로 추가

    // 끝 슬래시 제거 (예: "https://api.com/" → "https://api.com")
    return base.replace(/\/+$/, "");
}

export const BASE_URL = resolveBaseURL();

// 퍼블릭(비인증) 경로 목록 — 여기엔 Authorization 헤더를 붙이지 않음
const PUBLIC_PATHS = [
  "/api/users/signup",
  "/api/users/check-id",
  "/api/users/check-email",
  "/api/auth/login",
  "/api/auth/refresh",
  "/api/users/find-id",
  "/api/users/verify"
  "/api/users/reset-password"
];

function isPublic(url) {
  try {
    const u = new URL(url, BASE_URL);     // 상대/절대 모두 처리
    const path = u.pathname;              // e.g. /api/users/check-id
    return PUBLIC_PATHS.some((p) => path.startsWith(p));
  } catch {
    // url이 상대경로일 수도 있음
    return PUBLIC_PATHS.some((p) => url.startsWith(p));
  }
}

export const api = axios.create({
  baseURL: BASE_URL,
  withCredentials: true, // 쿠키 포함 요청 허용(리프레시 토큰 쿠키 전송)
  headers: { Accept: "application/json" }
});

export function unwrapApiResponse(res) {
  if (res && typeof res === "object" && "success" in res) return res;
  return { success: true, data: res, message: null, code: null };
}

//요청 인터셉터
//매 요청 전에 로컬 저장소에서 액세스 토큰을 꺼내 Authorization 헤더로 붙여줌.
api.interceptors.request.use((config) => {
  const access = localStorage.getItem("accessToken");
  if (access&&!isPublic(config.url || "")) {
    config.headers = config.headers || {};
    config.headers.Authorization = `Bearer ${access}`;
  }
  return config;
});


let isRefreshing = false;   //지금 /auth/refresh를 호출 중인지 표시하는 스위치.
let pendingQueue = [];      //갱신이 끝나길 기다리는 대기 요청들의 약속(resolve/reject)을 담는 배열.
const REFRESH_URL = "/api/auth/refresh";

// 응답 인터셉터
api.interceptors.response.use(
  (res) => {const wrapped = unwrapApiResponse(res?.data);
               if (wrapped.success === false) {
                 // 서버가 200 OK라 해도 success=false면 에러로 취급
                 const err = new Error(wrapped.message || "Request failed");
                 err.code = wrapped.code;
                 err.status = res?.status ?? 400;
                 return Promise.reject(err);
               }
               // 호출부가 편하게 쓰도록 표준화된 객체를 반환 (원하면 wrapped.data만 반환해도 됨)
               return wrapped;
  },
  async (error) => {
    const original = error?.config;     //실패한 원래 요청의 설정

    // 네트워크/취소 같은 케이스는 그대로
    if (!original) return Promise.reject(error);

    // refresh 호출 자체에서 또 refresh 하지 않도록
    const isRefreshCall =
    new URL(original.url, BASE_URL).pathname.startsWith(REFRESH_URL);

     const status = error?.response?.status;
     const code = error?.response?.data?.code;

     const isTokenExpired = status === 401 && code === "TOKEN_EXPIRED";

    if (isTokenExpired && original && !original._retry) {   //만료이며, 아직 재시도 안 했을 때만 특별 처리
      if (isRefreshing) {
        // 갱신 중이면 큐에 보관
        return new Promise((resolve, reject) => {
          pendingQueue.push({ resolve, reject });
        }).then((token) => {
          original.headers = original.headers || {};
          original.headers.Authorization = `Bearer ${token}`;
          return api(original);
        });
      }

      original._retry = true;
      isRefreshing = true;

      try {
        const refreshRes  = await axios.post(
           REFRESH_URL,
           {},
           { baseURL: BASE_URL, withCredentials: true, headers: { Accept: "application/json" } }
        );
        // 서버 응답이 ApiResponse<String> 또는 {accessToken: "..."} 둘 다 대응
        const wrapped = unwrapApiResponse(refreshRes?.data);
        const newAccess = wrapped?.data?.accessToken || wrapped?.data || refreshRes?.data?.accessToken;

        if (!newAccess) throw new Error("No access token in refresh response");

        localStorage.setItem("accessToken", newAccess);

        // 대기중이던 요청 처리
        pendingQueue.forEach((p) => p.resolve(newAccess));
        pendingQueue = [];
        isRefreshing = false;

        // 원요청 재시도
        original.headers = original.headers || {};
        original.headers.Authorization = `Bearer ${newAccess}`;
        return api(original);
      } catch (e) {
        // 실패 → 로그인 유도/홈으로 이동 등
        pendingQueue.forEach((p) => p.reject(e));
        pendingQueue = [];
        isRefreshing = false;
        localStorage.removeItem("accessToken");
        // 필요 시 라우팅:
//        window.location.href = '/';
        return Promise.reject(e);
      }
    }

    return Promise.reject(error);
  }
);

export default api;
