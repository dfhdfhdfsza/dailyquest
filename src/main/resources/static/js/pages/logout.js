// 공통: 토큰 키 후보
const TOKEN_KEYS = ["accessToken", "refreshToken"];

// 토큰 싹 정리
function clearTokens() {
      for (const k of TOKEN_KEYS) {
        localStorage.removeItem(k);
        sessionStorage.removeItem(k);
      }
      // axios 인스턴스(api) 쓰는 경우 Authorization 헤더 제거
      if (window.api?.defaults?.headers?.common.Authorization) {
        delete window.api.defaults.headers.common.Authorization;
      }
}

// UI 갱신(있다면)
function notifyLoggedOut() {
      try {
        // 같은 도메인의 다른 탭에게도 반영되도록(선택)
        localStorage.setItem("__logout_broadcast__", String(Date.now()));
        localStorage.removeItem("__logout_broadcast__");
      } catch {}
      // 페이지 이동
      location.replace("/");
}

const logoutBtn = document.getElementById("logoutBtn");
if (logoutBtn) {
  logoutBtn.addEventListener("click", async (e) => {
    e.preventDefault();

    // 중복 클릭 방지
    logoutBtn.disabled = true;

    try {
      // 세션 기반이라면 쿠키 포함
      const cfg = { withCredentials: true };

      // 서버에서 refresh 쿠키 무효화 + 서버 세션 종료(세션 방식일 때)
      await api.post("/logout", null, cfg); // 200/204 기대, 실패해도 finally에서 client 정리
    } catch (_) {
      // 네트워크/서버 오류가 있어도 클라이언트 상태는 반드시 정리
    } finally {
      clearTokens();
      notifyLoggedOut();
    }
  });
}

window.addEventListener("storage", (e) => {
  if (e.key === "__logout_broadcast__") {
    clearTokens();
    location.replace("/");
  }
});