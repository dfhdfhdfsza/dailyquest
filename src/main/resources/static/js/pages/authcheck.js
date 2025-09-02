document.addEventListener("DOMContentLoaded", () => {
  const TOKEN_KEYS = ["accessToken", "token"];

  // 요소 헬퍼
  const $ = (id) => document.getElementById(id);
  const loginBtn  = $("loginBtn");
  const logoutBtn = $("logoutBtn");
  const signupBtn = $("signupBtn");

  // 요소 표시/숨김: 원래 display 기억해서 복원
  // 처음 한 번 원래 display를 data-_origDisplay로 저장해둔 뒤, 다시 켤 때 그대로 복원
 function setVisible(el, isVisible) {
   if (!(el instanceof HTMLElement)) return;

   const STORE_KEY = "origDisplay"; // => data-orig-display

   // 원래 display 값을 한 번만 기록
   if (!el.dataset[STORE_KEY]) {
     const currentDisplay = getComputedStyle(el).display;
     el.dataset[STORE_KEY] =
     currentDisplay && currentDisplay !== "none" ? currentDisplay : "inline-block";
   }

   el.style.display = isVisible ? el.dataset[STORE_KEY] : "none";
 }
 // 문자열 토큰 유효성 가드
 const isValidTokenString = (v) =>
   typeof v === "string" &&
   v.trim() !== "" &&
   v !== "undefined" &&
   v !== "null";

    //로컬스토리지에 토큰이 하나라도 있는지 확인
  function hasToken() {
      const stores = [localStorage, sessionStorage]; // 둘 다 확인!
      for (const store of stores) {
        for (const key of TOKEN_KEYS) {
          const value = store.getItem(key);
          if (isValidTokenString(value) /* && looksLikeJwt(value) */) {
            return true;
          }
        }
      }
      return false;
  }


  function updateButtons() {
    const loggedIn = hasToken();

    setVisible(loginBtn,  !loggedIn);
    setVisible(signupBtn, !loggedIn);
    setVisible(logoutBtn,  loggedIn);
  }

  // 초기 반영
  updateButtons();

  // 다른 탭 변경 동기화(+ clear 대응)
  window.addEventListener("storage", (e) => {
    if (!e.key || TOKEN_KEYS.includes(e.key)) updateButtons();
  });

  // 같은 탭에서 setItem/removeItem을 호출할 때도 감지
  const _setItem = localStorage.setItem;
  localStorage.setItem = function (k, v) {
    const res = _setItem.apply(this, arguments);
    if (TOKEN_KEYS.includes(k)) updateButtons();
    return res;
  };

  const _removeItem = localStorage.removeItem;
  localStorage.removeItem = function (k) {
    const res = _removeItem.apply(this, arguments);
    if (TOKEN_KEYS.includes(k)) updateButtons();
    return res;
  };

  // 탭으로 돌아왔을 때 갱신
  window.addEventListener("visibilitychange", () => {
    if (document.visibilityState === "visible") updateButtons();
  });
  window.addEventListener("pageshow", updateButtons);

});
