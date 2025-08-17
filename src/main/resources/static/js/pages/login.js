import { api, BASE_URL } from '/js/apiClient.js';

document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("loginForm");
  const submitBtn = document.getElementById("submit");

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const loginId = document.getElementById("id-input").value.trim();
    const password = document.getElementById("pw-input").value.trim();
    if (!loginId || !password) {
      alert("아이디와 비밀번호를 모두 입력해주세요.");
      return;
    }

    submitBtn.disabled = true; // 중복 클릭 방지
    try {
      // fingerprint 생성
      const fpResult = await FingerprintJS.load().then(fp => fp.get());
      const fingerprint = fpResult.visitorId;

      // 로그인 요청
      const response = await fetch("api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ loginId, password, fingerprint })
      });
        console.log(response.body);
      if (!response.ok) {
        const errorText = await response.text().catch(() => "로그인 실패");
        alert(`로그인 실패: ${errorText}`);
        return;
      }

      // 서버는 { accessToken: "...", user: {...} } 형태로 응답
      const { accessToken } = await response.json();

      // 액세스 토큰 저장 (키 이름도 일관되게)
      localStorage.setItem("accessToken", accessToken);

      alert("로그인 성공");
      window.location.href = "/";
    } catch (err) {
      console.error("로그인 중 오류 발생:", err);
      alert("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    } finally {
      submitBtn.disabled = false;
    }
  });
});
