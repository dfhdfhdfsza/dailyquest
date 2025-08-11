document.addEventListener("DOMContentLoaded", function () { // 이벤트 등록

      const form = document.getElementById("signup-form");
      const idInput = document.getElementById("id-input");
      const idResult = document.getElementById("id-check-result");
      const emailIdInput = document.getElementById("email-id");
      const domainSelect = document.getElementById("email-domain");
      const customDomain = document.getElementById("custom-domain");
      const fullEmailHidden = document.getElementById("full-email");
      const submitBtn = document.getElementById("submit");
      const emailResult = document.getElementById("email-check-result");
      const passwordInput=document.getElementById("pw-input");
      const idValidateSpan=document.getElementById("id-validate-result");
      const pwValidateSpan=document.getElementById("pw-validate-result");
      const emailValidateSpan=document.getElementById("email-validate-result");

      //local@domain 형태를 local, domain으로 분리
      const [local, domain] = fullEmailHidden.value.split('@');

        if (local && domain) {
          emailIdInput.value = local;

          // select에 domain을 맞추고 없으면 custom에 넣기

          //<select>의 option들을 배열로 펼쳐서, 도메인 값이 옵션에 존재하는지 확인
          if ([...emailIdInput.options].some(o => o.value === domain)) {
            emailIdInput.value = domain;
            customDomain.style.display = 'none';
            customDomain.required = false;
            customDomain.value = '';
          } else {
            emailIdInput.value = 'custom';
            customDomain.style.display = 'inline';
            customDomain.required = true;
            customDomain.value = domain;
          }
        }


      // 입력이 매우 잦은 이벤트에서 서버로 과도한 요청을 보내지 않게 400ms 동안 추가 입력이 없을 때만 fn을 호출
      function debounce(fn, delay = 400) {
            let t;
            return (...args) => {
              clearTimeout(t);
              t = setTimeout(() => fn(...args), delay);
            };
      }

      // 이메일 도메인에서 ‘직접입력’ 선택 시 추가 입력창을 보였다 숨김
      function handleDomainChange() {
            if (domainSelect.value === "custom") {    //직접입력을 선택 했을 시 입력창 활성화
              customDomain.style.display = "inline";
              customDomain.required = true;
            } else {                          //직접입력을 선택 안했을 시 입력창 비활성화
              customDomain.style.display = "none";
              customDomain.required = false;
              customDomain.value = "";
            }
            updateFullEmail();
      }

      //비동기로 이메일 아이디와 도메인을 합치는 fucntion
      function updateFullEmail() {
            const emailId = document.getElementById("email-id").value;    //email 아이디부분
            const domain = document.getElementById("email-domain").value === "custom"
              ? document.getElementById("custom-domain").value.trim()
              : document.getElementById("email-domain").value;

            //이메일아이디와 도메인 결합
            const fullEmail = (emailId && domain) ? `${emailId}@${domain}` : "";
            document.getElementById("full-email").value = fullEmail;

            debouncedCheckEmail();
      }

       //이메일 정규식 검사 function
       function isValidEmailFormat(email) {
           const pattern = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
           return pattern.test(email);
       }
        //아이디 정규식 검사 function
       function isValidIdFormat(id) {
           // 영문으로 시작, 영문/숫자/_/-, 길이 8~20
           const pattern = /^[A-Za-z][A-Za-z0-9_-]{8,19}$/;
           return pattern.test(id);
       }

       //비밀번호 정규식 검사 function
       function isValidPasswordFormat(pw) {
           // 길이 8~20, 반드시 영문 1개 이상 + 숫자 1개 이상 포함
           const pattern = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d!"#$%&'()*+,\-./:;<=>?@\[\]\\^_`{|}~]{8,20}$/;
           return pattern.test(pw);
       }

        //비밀번호 정규식 검사 이벤트
       passwordInput.addEventListener("input",async function(e){
            const pw=(passwordInput.value||'').trim();

            if(!isValidPasswordFormat(pw)){
                pwValidateSpan.textContent="길이 8~20, 반드시 영문 1개 이상 + 숫자 1개 이상 포함";
                pwValidateSpan.style.color = "red";
            }else{
                pwValidateSpan.textContent="";
            }
       })

        //아이디 정규식 검사 이벤트
       idInput.addEventListener("input",async function(e){
              const id=(idInput.value||'').trim();

              if(!isValidIdFormat(id)){
                  idValidateSpan.textContent="영문으로 시작, 영문/숫자/_/-, 길이 8~20";
                  idValidateSpan.style.color = "red";
              }else{
                  idValidateSpan.textContent="";
              }
       })


      //아이디 중복 검사
      const debouncedCheckId = debounce(async function checkId() {
            const id = (idInput.value || '').trim();
            if (!id||!isValidIdFormat(id)) {
              idResult.textContent = '';
              idResult.removeAttribute('style');
              return;
            }
            try {
              const res = await fetch(`/api/users/check-id?id=${encodeURIComponent(id)}`, {
                headers: { 'Accept': 'application/json' }
              });
              if (!res.ok) throw new Error('network');

              const exists = (res.headers.get('content-type') || '').includes('application/json')
                ? await res.json(): null;

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

      //페이지 첫 접속시 이메일 유효성 검사 경고 메시지를 지우기 위한 변수
      let emailTouched=false;

      //이메일에 변경이 일어날때 addeventlistner할 function
      function markEmailTouched() { emailTouched = true; }

        //이메일 중복 검사
        //유효한 형식일 때만 중복 체크 수행
      const debouncedCheckEmail = debounce(async function checkEmail() {
            const email = (fullEmailHidden.value || '').trim();

            emailValidateSpan.textContent='';

            //값이 없거나 형식이 맞지않으면 이메일결과창 초기화 후 종료
            if (!email || !isValidEmailFormat(email)) {
              emailResult.textContent = '';
              emailResult.removeAttribute('style');
              if(emailTouched){
                emailValidateSpan.textContent="올바르지 않은 이메일 형식입니다."
                emailValidateSpan.style.color="red";

              }
              submitBtn.disabled = false;
              return;
            }

            //이메일 중복 체크 요청
            try {
              const res = await fetch(`/api/users/check-email?email=${encodeURIComponent(email)}`, {
                headers: { 'Accept': 'application/json' }
              });
              if (!res.ok) throw new Error('network');

              // 응답이 JSON인지 안전하게 확인 후 파싱
              const exists = (res.headers.get('content-type') || '').includes('application/json')
                ? await res.json(): null;

              if (exists) {
                emailResult.textContent = "중복된 이메일입니다.";
                emailResult.style.color = "red";
                submitBtn.disabled = true;
              } else {
                emailResult.textContent = "사용 가능한 이메일입니다.";
                emailResult.style.color = "green";
                submitBtn.disabled = false;
              }
            } catch (e) {
              emailResult.textContent = "이메일 확인 실패. 잠시 후 다시 시도해주세요.";
              emailResult.style.color = "orange";
              submitBtn.disabled = false;
            }
      }, 400);

      // 이벤트 바인딩
      idInput.addEventListener("input", debouncedCheckId);
      emailIdInput.addEventListener("input",()=>{ updateFullEmail(); markEmailTouched(); });
      domainSelect.addEventListener("change", ()=>{ handleDomainChange(); markEmailTouched(); });
      customDomain.addEventListener("input", ()=>{ updateFullEmail(); markEmailTouched(); });

      // 초기 한 번 동기화
      updateFullEmail();

      // 폼 제출 전 최종 형식 검증(UX용)
      form.addEventListener("submit", function (e) {
        const email = document.getElementById("full-email").value;
        const id = document.getElementById("id-input").value;
        const pw = document.getElementById("pw-input").value;

        let valid = true;

        if (!isValidIdFormat(id)) {
                  alert("아이디 형식이 올바르지 않습니다.");
                 valid = false;
                 e.preventDefault(); //폼전송 막기
                 return;
        }
        if (!isValidPasswordFormat(pw)) {
                  alert("비밀번호 형식이 올바르지 않습니다.");
                  valid = false;
                  e.preventDefault(); //폼전송 막기
                  return;
        }

        if (!isValidEmailFormat(email)) {
          alert("이메일 형식이 올바르지 않습니다.");
          valid = false;
          e.preventDefault(); //폼전송 막기
          return;
        }
      })
});