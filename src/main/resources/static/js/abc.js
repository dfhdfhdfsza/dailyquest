 // Base64URL → JSON
  function parseJwt(token) {
    try {
      const base64Url = token.split(".")[1];
      if (!base64Url) return null;
      let base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
      while (base64.length % 4) base64 += "=";
      const json = atob(base64);
      return JSON.parse(json);
    } catch { return null; }
  }

  function fmt(tsSec) {
    if (!tsSec) return "-";
    const d = new Date(tsSec * 1000);
    return `${d.toLocaleString()}`;
  }

  function renderAuth() {
    const token = localStorage.getItem("accessToken");
    const $state = document.getElementById("auth-state");
    const $detail = document.getElementById("auth-detail");
    if (!token) {
      $state.textContent = "로그아웃 상태";
      $detail.style.display = "none";
      return;
    }
    const payload = parseJwt(token) || {};
    // 자주 쓰는 클레임 후보: sub / username / roles / authorities / exp
    const username = payload.username || payload.sub || "-";
    const roles =
      (payload.roles && payload.roles.join?.(",")) ||
      (payload.authorities && payload.authorities.join?.(",")) ||
      payload.role || "-";

    document.getElementById("auth-username").textContent = username;
    document.getElementById("auth-role").textContent = roles;
    document.getElementById("auth-iat").textContent  = fmt(payload.iat); // 발급일
    document.getElementById("auth-exp").textContent = fmt(payload.exp);

    $state.textContent = "로그인 상태";
    $detail.style.display = "";
  }


  // 초기 렌더 + 다른 탭 변경 반영
  renderAuth();
  window.addEventListener("storage", (e) => {
    if (e.key === "accessToken") renderAuth();
  });