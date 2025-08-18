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

  // 영문 대소문자 + 숫자만 허용/ 4~20자
  const isValidIdFormat = (id) =>
    /^[a-zA-Z0-9]{4,20}$/.test(id);

  // 영문 최소 1개 이상/숫자 최소 1개 이상/특수문자 최소 1개 이상/8~20자 제한
  const isValidPasswordFormat = (pw) =>
    /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,20}$/.test(pw);


  // ApiResponse 표준: {success, data, message, code}
  // 체크 계열이 true/false 또는 {data:true/false}로 와도 안전하게 boolean으로 변환
  const toBoolean = (v) => typeof v === "boolean" ? v : !!v;

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
  //비밀번호 입력시 검증
  passwordInput.addEventListener("input", () => {
        const pw = (passwordInput.value || "").trim();
        if (!isValidPasswordFormat(pw)) {
          pwValidateSpan.textContent = "영문 최소 1개 이상/숫자 최소 1개 이상/특수문자 최소 1개 이상/8~20자 제한";
          pwValidateSpan.style.color = "red";
        } else {
          pwValidateSpan.textContent = "";
        }
  });

  //아이디 입력시 검증
  idInput.addEventListener("input", () => {
        const id = (idInput.value || "").trim();
        if (!isValidIdFormat(id)) {
          idValidateSpan.textContent = "영문 대소문자 + 숫자만 허용 / 4~20자";
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
           // 응답은 { success, data, ... } 로 표준화되어 옴
           const { data } = await api.get("/users/check-id", {
               params: { id }
           });
          const exists = toBoolean(data);

          if (exists) {
                  idResult.textContent = "이미 사용중인 아이디입니다.";
                  idResult.style.color = "red";
          } else {
                  idResult.textContent = "사용 가능한 아이디입니다.";
                  idResult.style.color = "green";
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
          const { data } = await api.get("/users/check-email", {
               params: { email }
          });

          const exists = toBoolean(data);

          if (exists) {
              emailResult.textContent = "중복된 이메일입니다.";
              emailResult.style.color = "red";
          } else {
              emailResult.textContent = "사용 가능한 이메일입니다.";
              emailResult.style.color = "green";
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
      const result = await api.post("/users/signup", dto);
      serverSuccess.textContent = result.message || "회원가입 성공!";
      // 필요한 라우팅
      window.location.href = "/";
    } catch (err) {
      serverError.textContent = err.message || "회원가입 중 오류가 발생했습니다.";
    } finally {
      signupBtn.disabled = false;
    }
  }

  signupBtn.addEventListener("click", handleSignup);
});
