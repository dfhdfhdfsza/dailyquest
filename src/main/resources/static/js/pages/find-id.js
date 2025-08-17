import { api, BASE_URL } from '/js/apiClient.js';

    const $ = (s)=>document.querySelector(s);

    $('#send-btn').addEventListener('click', async () => {
      const email = $('#email').value.trim();
      if (!email) return alert('이메일을 입력하세요');

      try {
        const res = await api.post('/users/find-id', { email }); // ← JSON으로 전송
        alert(res.message || '이메일로 아이디를 전송했습니다.');
        window.location.href = "/";
      } catch (e) {
        alert(e.message || '전송 실패');
      }
    });