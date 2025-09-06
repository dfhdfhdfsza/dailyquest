function safeGetToken() {
  const pick = (v) => (v && v !== "undefined" && v !== "null" ? v : null);
  return pick(localStorage.getItem("accessToken")) ||
         pick(sessionStorage.getItem("accessToken")) ||
         null;
}

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

//  누락돼 있던 저장 함수 추가 (세션 저장 권장)
function setAccessToken(token) {
  sessionStorage.setItem("accessToken", token);
  // 필요 시 로컬 저장:
  // localStorage.setItem("accessToken", token);
}


function fmt(tsSec) {
  if (!tsSec) return "-";
  const d = new Date(tsSec * 1000);
  return `${d.toLocaleString()}`;
}

function renderAuth() {
  const token = safeGetToken();
  const $state = document.getElementById("auth-state");
  const $detail = document.getElementById("auth-detail");
  if (!$state) return;

  if (!token) {
    $state.textContent = "로그아웃 상태";
    $detail.style.display = "none";
    return;
  }
  const payload = parseJwt(token) || {};
  const username = payload.username || payload.sub || "-";
  const roles =
    (payload.roles && payload.roles.join?.(",")) ||
    (payload.authorities && payload.authorities.join?.(",")) ||
    payload.role || "-";

  document.getElementById("auth-username").textContent = username;
  document.getElementById("auth-role").textContent = roles;
  document.getElementById("auth-iat").textContent  = fmt(payload.iat);
  document.getElementById("auth-exp").textContent  = fmt(payload.exp);

  $state.textContent = "로그인 상태";
  $detail.style.display = "";
}

// code가 있으면 교환 → 저장 → URL 정리
async function exchangeCodeIfPresent() {
  const params = new URLSearchParams(location.search);
  const code = params.get("code");
  if (!code) return false;

  try {
    const res = await fetch("/api/auth/social/exchange", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include", // 서버가 refresh HttpOnly 쿠키를 세팅하도록
      body: JSON.stringify({ code }),
    });

    if (!res.ok) {
      // 만료/재사용 등 실패 → 로그인으로 돌려보내기
      console.warn("exchange failed:", res.status);
      location.replace("/login");
      return false;
    }

    const data = await res.json(); // { accessToken: "..." } 를 가정
    console.log(data);
    if (data?.data.accessToken) {
      setAccessToken(data.data.accessToken);
    }

    // URL 정리 (code 제거; 다른 쿼리가 있으면 유지)
      params.delete("code");
      const clean = params.toString();
      const cleanUrl = clean ? `/?${clean}` : "/";
      history.replaceState(null, "", cleanUrl);

      window.dispatchEvent(new Event("auth-updated"));
    return true;
  } catch (e) {
    console.error("exchange error:", e);
    location.replace("/login");
    return false;
  }
}

document.addEventListener("DOMContentLoaded", () => {
    (async () => {
      // 1) code 있으면 교환
      await exchangeCodeIfPresent();
      // 2) 현재 상태 렌더
      await renderAuth();
    })();
  window.addEventListener("storage", (e) => {
    if (e.key === "accessToken") renderAuth();
  });
});