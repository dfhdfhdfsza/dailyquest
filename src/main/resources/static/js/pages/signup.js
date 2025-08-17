import { api } from '/js/apiClient.js';

document.addEventListener("DOMContentLoaded", () => {
  // 엘리먼트

  const usernameInput = document.getElementById("username");
  const idInput = document.getElementById("id-input");
  const idResult = document.getElementById("id-check-result");
  const idValidateSpan = document.getElementById("id-validate-result");

  const passwordInput = document.getElementById("pw-input");
  const pwValidateSpan = document.getElementById("pw-validate-result");

  const emailIdInput = document.getElementById("email-id");
  const domainSelect = document.getElementById("email-domain");
  const customDomain = document.getElementById("custom-domain");
  const emailResult = document.getElementById("email-check-result");
  const emailValidateSpan = document.getElementById("email-validate-result");

  const signupBtn = document.getElementById("signup-btn");
  const serverError = document.getElementById("server-error");
  const serverSuccess = document.getElementById("server-success");

  // -------- 유틸 --------
  const debounce = (fn, delay = 400) => {
    let t;
    return (...args) => {
      clearTimeout(t);
      t = setTimeout(() => fn(...args), delay);
    };
  };

  const isValidEmailFormat = (email) =>
    /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(email);

  // 영문으로 시작, 영문/숫자/_/-, 총 8~20자
  const isValidIdFormat = (id) =>
    /^[A-Za-z][A-Za-z0-9_-]{7,19}$/.test(id);

  // 길이 8~20, 반드시 영문 1개 이상 + 숫자 1개 이상
  const isValidPasswordFormat = (pw) =>
    /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d!"#$%&'()*+,\-./:;<=>?@\[\]\\^_`{|}~]{8,20}$/.test(pw);

  const isJsonResponse = (res) =>
    (res.headers.get("content-type") || "").includes("application/json");

  async function postJson(url, body) {
        const res = await fetch(url, {
          method: "POST",
          headers: { "Content-Type": "application/json", Accept: "application/json" },
          body: JSON.stringify(body),
          credentials: "include", // 필요 시 유지
        });
          const ct = res.headers.get("content-type") || "";
          let data = null;

          try {
            if (ct.includes("application/json")) {
              // 빈 바디면 JSON.parse 에러가 나므로 먼저 text로 받고 비어있으면 null
              const txt = await res.text();
              data = txt ? JSON.parse(txt) : null;
            } else if (res.status !== 204) {
              // JSON이 아니면 텍스트로 받아서 디버깅에 활용
              data = await res.text();
            }
          } catch (e) {
            // 파싱 실패해도 아래에서 상태코드로 처리
            console.warn("JSON parse failed:", e);
          }
        // ApiResponse 규격을 우선 지원
        const isApi = data && typeof data === "object" && "success" in data;

        if (!res.ok || (isApi && data.success === false)) {
          const msg = isApi
            ? (data.message || `HTTP ${res.status}`)
            : (typeof data === "string" && data) || `HTTP ${res.status}`;
          const err = new Error(msg);
          err.code = isApi ? data.code : undefined;
          err.status = res.status;
          // 디버깅 도움
          console.error("postJson error", { url, status: res.status, ct, data });
          throw err;
        }

        // 호출부가 항상 같은 형태로 쓰게 포맷 정규화
        return isApi ? data : { success: true, data, message: null, code: null };
  }



  const getEmail = () => {
        const local = (emailIdInput.value || "").trim();
        const domain =
          domainSelect.value === "custom"
            ? (customDomain.value || "").trim()
            : domainSelect.value;
        return local && domain ? `${local}@${domain}` : "";
      };

      // -------- 이메일 도메인 UI --------
      function handleDomainChange() {
        if (domainSelect.value === "custom") {
          customDomain.style.display = "inline";
          customDomain.required = true;
        } else {
          customDomain.style.display = "none";
          customDomain.required = false;
          customDomain.value = "";
        }
        debouncedCheckEmail();
      }
      domainSelect.addEventListener("change", handleDomainChange);

      // -------- 즉시 검증 --------
      passwordInput.addEventListener("input", () => {
        const pw = (passwordInput.value || "").trim();
        if (!isValidPasswordFormat(pw)) {
          pwValidateSpan.textContent = "길이 8~20, 반드시 영문 1개 이상 + 숫자 1개 이상 포함";
          pwValidateSpan.style.color = "red";
        } else {
          pwValidateSpan.textContent = "";
        }
      });

      idInput.addEventListener("input", () => {
        const id = (idInput.value || "").trim();
        if (!isValidIdFormat(id)) {
          idValidateSpan.textContent = "영문으로 시작, 영문/숫자/_/-, 길이 8~20";
          idValidateSpan.style.color = "red";
        } else {
          idValidateSpan.textContent = "";
        }
  });

  // -------- 아이디 중복 검사 --------
  const debouncedCheckId = debounce(async () => {
        const id = (idInput.value || "").trim();
        if (!id || !isValidIdFormat(id)) {
          idResult.textContent = "";
          idResult.removeAttribute("style");
          return;
        }
        try {
          const res = await fetch(`/api/users/check-id?id=${encodeURIComponent(id)}`, {
            headers: { Accept: "application/json" },
          });
          if (!res.ok) throw new Error("network");

          if (isJsonResponse(res)) {
            const payload = await res.json();
            const exists = typeof payload === "boolean" ? payload : payload.data ?? payload;
            if (exists) {
              idResult.textContent = "이미 사용중인 아이디입니다.";
              idResult.style.color = "red";
            } else {
              idResult.textContent = "사용 가능한 아이디입니다.";
              idResult.style.color = "green";
            }
          } else {
            idResult.textContent = "";
            idResult.removeAttribute("style");
          }
        } catch (e) {
          idResult.textContent = "중복 확인 실패. 잠시 후 다시 시도해주세요.";
          idResult.style.color = "orange";
        }
      }, 400);
      idInput.addEventListener("input", debouncedCheckId);

      // -------- 이메일 중복 + 형식 검사 --------
      let emailTouched = false;
      const markEmailTouched = () => (emailTouched = true);

      emailIdInput.addEventListener("input", () => {
        markEmailTouched();
        debouncedCheckEmail();
      });
      customDomain.addEventListener("input", () => {
        markEmailTouched();
        debouncedCheckEmail();
      });

      const debouncedCheckEmail = debounce(async () => {
        emailValidateSpan.textContent = "";

        const email = getEmail();
        if (!email || !isValidEmailFormat(email)) {
          emailResult.textContent = "";
          emailResult.removeAttribute("style");
          if (emailTouched) {
            emailValidateSpan.textContent = "올바르지 않은 이메일 형식입니다.";
            emailValidateSpan.style.color = "red";
          }
          return;
        }
        try {
          const res = await fetch(`/api/users/check-email?email=${encodeURIComponent(email)}`, {
            headers: { Accept: "application/json" },
          });
          if (!res.ok) throw new Error("network");

          if (isJsonResponse(res)) {
            const payload = await res.json();
            const exists = typeof payload === "boolean" ? payload : payload.data ?? payload;
            if (exists) {
              emailResult.textContent = "중복된 이메일입니다.";
              emailResult.style.color = "red";
            } else {
              emailResult.textContent = "사용 가능한 이메일입니다.";
              emailResult.style.color = "green";
            }
          } else {
            emailResult.textContent = "";
            emailResult.removeAttribute("style");
          }
        } catch (e) {
          emailResult.textContent = "이메일 확인 실패. 잠시 후 다시 시도해주세요.";
          emailResult.style.color = "orange";
        }
  }, 400);

  // -------- 제출 (클릭 이벤트로 JSON 전송) --------
  async function handleSignup() {
    serverError.textContent = "";
    serverSuccess.textContent = "";

    const username = (usernameInput.value || "").trim();
    const loginId = (idInput.value || "").trim();
    const password = (passwordInput.value || "").trim();
    const email = getEmail();

    // UX용 최소 검증 (최종 검증은 서버 @Valid가 담당)
    if (!username) return alert("닉네임을 입력해주세요.");
    if (!isValidIdFormat(loginId)) return alert("아이디 형식이 올바르지 않습니다.");
    if (!isValidPasswordFormat(password)) return alert("비밀번호 형식이 올바르지 않습니다.");
    if (!isValidEmailFormat(email)) return alert("이메일 형식이 올바르지 않습니다.");

    signupBtn.disabled = true;
    try {
      const dto = { username, loginId, password, email };
      const result = await postJson("/api/users/signup", dto);
      serverSuccess.textContent = result.message || "회원가입 성공!";
      // 필요한 라우팅
      window.location.href = "/login";
    } catch (err) {
      serverError.textContent = err.message || "회원가입 중 오류가 발생했습니다.";
    } finally {
      signupBtn.disabled = false;
    }
  }

  signupBtn.addEventListener("click", handleSignup);

  // 엔터키로도 제출되게 하고 싶으면(폼이 없으니 수동 처리):
  document.getElementById("signup").addEventListener("keydown", (e) => {
    if (e.key === "Enter") {
      e.preventDefault(); // 우발적 페이지 리로드 방지
      handleSignup();
    }
  });
});
