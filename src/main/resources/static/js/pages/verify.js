import { api } from '/js/apiClient.js';

    const $ = (s)=>document.querySelector(s);
    const emailRe = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;

    $('#verify-btn').addEventListener('click', async () => {
      const loginId = $('#loginId').value.trim();
      const email   = $('#email').value.trim();
      const msgEl   = $('#msg');
      msgEl.textContent = '';

      if (!loginId) { msgEl.textContent = '아이디를 입력하세요.'; return; }
      if (!emailRe.test(email)) { msgEl.textContent = '올바른 이메일을 입력하세요.'; return; }

      try {
        // baseURL=/api 이므로 상대 경로 사용
        const res = await api.post('/users/verify', { loginId, email });
        // ApiResponse<VerifyResult> 가정: { success, data:{ token }, message }
        const token = typeof res?.data === 'string' ? res.data : res?.data?.token;
        if (!token) throw new Error('토큰이 응답에 없습니다.');

        // 방법 A) 쿼리스트링으로 화면 이동
        window.location.href = `/reset-password?token=${encodeURIComponent(token)}`;

        // 방법 B) 토큰을 세션스토리지에 저장 후 /reset-password 진입에서 꺼내 쓰고 싶다면:
        // sessionStorage.setItem('pw_reset_token', token);
        // window.location.href = '/reset-password';

      } catch (e) {
        msgEl.textContent = e?.message || '본인인증 요청이 실패했습니다.';
      }
    });