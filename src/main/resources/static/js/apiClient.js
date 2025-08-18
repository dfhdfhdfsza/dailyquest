//Axios를 CDN에서 ESM으로 불러옴.
import axios from "https://cdn.jsdelivr.net/npm/axios@1.6.7/+esm";

const defaultPath = "/api";

// 사용할 API baseURL을 결정
function resolveBaseURL() {
  let base =
      window.__ENV?.API_BASE_URL || //window 전역에 주입된 환경변수 우선
      document.querySelector('meta[name="api-base-url"]')?.content ||   // <meta> 태그에서 읽기
      (location.origin + defaultPath); // 기본값: 현재 origin + "/api"

    // 끝 슬래시 제거 (예: "https://api.com/" → "https://api.com")
    return base.replace(/\/+$/, "");
}

export const BASE_URL = resolveBaseURL();

// 퍼블릭(비인증) 경로 목록 — 여기엔 Authorization 헤더를 붙이지 않음
const PUBLIC_PATHS = [
//  "/api/users/signup",
//  "/api/users/check-id",
//  "/api/users/check-email",
  "/api/auth/login",
  "/api/auth/refresh",
//  "/api/users/find-id",
//  "/api/users/verify",
//  "/api/users/reset-password"
];

// 현재 URL이 PUBLIC_PATHS에 해당하는지 확인
function isPublic(url) {
  try {
    const u = new URL(url, BASE_URL);     // 상대/절대 모두 처리
    const path = u.pathname;              // e.g. /api/users/check-id
    return PUBLIC_PATHS.some((p) => path.startsWith(p));
  } catch {
    // URL 생성이 실패하면 그냥 문자열 비교
    return PUBLIC_PATHS.some((p) => url.startsWith(p));
  }
}

// Axios 인스턴스 생성
export const api = axios.create({
  baseURL: BASE_URL,
  withCredentials: true, // 쿠키 포함 요청 허용(리프레시 토큰 쿠키 전송)
  headers: { Accept: "application/json" }
});

//API 응답 포맷 통일
export function unwrapApiResponse(res) {
   // {success, data, message, code} 형태면 그대로
  if (res && typeof res === "object" && "success" in res) return res;
  // 일반 객체나 원시값이면 success=true로 감싸줌
  return { success: true, data: res, message: null, code: null };
}

//요청 인터셉터
//매 요청 전에 로컬 저장소에서 액세스 토큰을 꺼내 Authorization 헤더를 붙여줌.
api.interceptors.request.use((config) => {
  const access = localStorage.getItem("accessToken");
  if (access&&!isPublic(config.url || "")) {
    config.headers = config.headers || {};
    config.headers.Authorization = `Bearer ${access}`;
  }
  return config;
});


let isRefreshing = false;   //현재 refresh API 호출 중 여부
let pendingQueue = [];      // refresh 완료 후 재시도 대기중인 요청들
const REFRESH_URL = "/api/auth/refresh";

// 응답 인터셉터
api.interceptors.response.use(
  // 1) 성공 응답 처리
  (res) => {
    const wrapped = unwrapApiResponse(res?.data);
    if (wrapped.success === false) {
        // 서버가 200 OK라 해도 success=false면 에러로 취급
        const err = new Error(wrapped.message || "Request failed");
        err.code = wrapped.code;
        err.status = res?.status ?? 400;
        return Promise.reject(err);
    }
    // 정상 응답은 표준화된 객체 반환
    return wrapped;
  },
  // 2) 실패 응답 처리
  async (error) => {
    const original = error?.config;     //실패한 원래 요청의 설정

    // 네트워크/취소 같은 케이스는 그대로
    if (!original) return Promise.reject(error);

    // refresh 호출 자체에서 또 refresh 하지 않도록
    const isRefreshCall =
    new URL(original.url, BASE_URL).pathname.startsWith(REFRESH_URL);

     // 401 + TOKEN_EXPIRED → 액세스 토큰 만료
     const status = error?.response?.status;
     const code = error?.response?.data?.code;
     const isTokenExpired = status === 401 && code === "TOKEN_EXPIRED";

    if (isTokenExpired && original && !original._retry) {
      //만료이며, 아직 재시도 안 했을 때만 특별 처리
      if (isRefreshing) {
        // 다른 refresh 진행 중이면 큐에 등록 → refresh 끝난 후 재요청
        return new Promise((resolve, reject) => {
          pendingQueue.push({ resolve, reject });
        }).then((token) => {
          original.headers = original.headers || {};
          original.headers.Authorization = `Bearer ${token}`;
          return api(original);
        });
      }
      // 재시도 플래그, refresh 시작
      original._retry = true;
      isRefreshing = true;

      try {
        // refresh API 호출
        const refreshRes  = await axios.post(
           REFRESH_URL,
           {},
           { baseURL: BASE_URL, withCredentials: true, headers: { Accept: "application/json" } }
        );
        // 서버 응답이 ApiResponse<String> 또는 {accessToken: "..."} 둘 다 대응
        const wrapped = unwrapApiResponse(refreshRes?.data);
        const newAccess = wrapped?.data?.accessToken || wrapped?.data || refreshRes?.data?.accessToken;

        if (!newAccess) throw new Error("No access token in refresh response");

        // 새 토큰 저장
        localStorage.setItem("accessToken", newAccess);

        // 대기중이던 요청 처리
        pendingQueue.forEach((p) => p.resolve(newAccess));
        pendingQueue = [];
        isRefreshing = false;

        // 실패했던 원 요청 다시 시도
        original.headers = original.headers || {};
        original.headers.Authorization = `Bearer ${newAccess}`;
        return api(original);
      } catch (e) {
        // refresh 실패 → 모든 대기 요청 실패 처리
        pendingQueue.forEach((p) => p.reject(e));
        pendingQueue = [];
        isRefreshing = false;
        localStorage.removeItem("accessToken"); // 기존 토큰 제거

        window.location.href = '/';
        return Promise.reject(e);
      }
    }

    return Promise.reject(error);   // 기타 에러는 그대로 반환
  }
);

export default api;
