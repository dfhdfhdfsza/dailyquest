//Axios를 CDN에서 ESM으로 불러옴 (이 파일은 <script type="module">로 로드해야 함).
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

const BASE_URL = resolveBaseURL();

const api = axios.create({
  baseURL: BASE_URL,
  withCredentials: true, // 쿠키 포함 요청 허용(리프레시 토큰 쿠키 전송)
});

//요청 인터셉터
//매 요청 전에 로컬 저장소에서 액세스 토큰을 꺼내 Authorization 헤더로 붙여줌.
api.interceptors.request.use((config) => {
  const access = localStorage.getItem("accessToken");
  if (access) {
    config.headers = config.headers || {};
    config.headers.Authorization = `Bearer ${access}`;
  }
  return config;
});


let isRefreshing = false;   //지금 /auth/refresh를 호출 중인지 표시하는 스위치.
let pendingQueue = [];      //갱신이 끝나길 기다리는 대기 요청들의 약속(resolve/reject)을 담는 배열.

// 응답 인터셉터
api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const original = error?.config;     //실패한 원래 요청의 설정
    const isTokenExpired =              //서버가 401이고 바디가 {"code":"TOKEN_EXPIRED"}일 때만 “만료”로 간주
      error?.response?.status === 401 &&
      error?.response?.data?.code === "TOKEN_EXPIRED";

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
        const { data } = await axios.post(
          "/auth/refresh",
          {},
          { baseURL: BASE_URL, withCredentials: true }
        );
        const newAccess = data.accessToken;
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
