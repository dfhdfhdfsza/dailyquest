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


function fmt(tsSec) {
  if (!tsSec) return "-";
  const d = new Date(tsSec * 1000);
  return `${d.toLocaleString()}`;
}

function renderAuth() {
  const token = safeGetToken();
  const $state = document.getElementById("auth-state");
  const $detail = document.getElementById("auth-detail");

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

document.addEventListener("DOMContentLoaded", () => {
  renderAuth();
  window.addEventListener("storage", (e) => {
    if (e.key === "accessToken") renderAuth();
  });
});