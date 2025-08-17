// /js/pages/reset-password.js
import { api } from '/js/apiClient.js';

const $ = (s) => document.querySelector(s);
const pw1 = $('#password');
const pw2 = $('#password2');
const btn = $('#reset-btn');
const msg = $('#msg');

// URL ?token=... 에서 토큰 읽기
const token = new URLSearchParams(location.search).get('token');
// 토큰이 쿼리에 없다면(세션스토리지에 저장해둔 경우를 쓰고 싶다면 아래 라인 사용)
// const token = new URLSearchParams(location.search).get('token') || sessionStorage.getItem('pw_reset_token');

function validPw(pw) {
  // 길이 8~12, 영문/숫자 최소 1자 포함 (원하는 정책으로 조정)
  return /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d!"#$%&'()*+,\-./:;<=>?@\[\]\\^_`{|}~]{8,12}$/.test(pw);
}

function refreshButtonState() {
  const a = pw1.value.trim();
  const b = pw2.value.trim();
  const ok = a === b && validPw(a);
  btn.disabled = !ok;
}
pw1.addEventListener('input', refreshButtonState);
pw2.addEventListener('input', refreshButtonState);

// 제출
btn.addEventListener('click', async () => {
  msg.textContent = '';
  const newPassword = pw1.value.trim();
  const newPasswordConfirm = pw2.value.trim();

  if (!token) {
    msg.textContent = '유효하지 않은 접근입니다. 본인인증을 다시 진행해주세요.';
    return;
  }
  if (newPassword !== newPasswordConfirm || !validPw(newPassword)) {
    msg.textContent = '비밀번호 조건을 확인해주세요.';
    return;
  }

  try {
    // baseURL=/api 이므로 상대 경로 사용
    const res = await api.post('/users/reset-password', {
      token,
      newPassword,
      newPasswordConfirm
    });
    // ApiResponse 규격: { success, data, message, code }
    msg.style.color = 'green';
    msg.textContent = res.message || '비밀번호가 변경되었습니다. 로그인 페이지로 이동합니다.';
    // 완료 후 이동
    setTimeout(() => (window.location.href = '/login'), 800);
  } catch (e) {
    msg.style.color = 'red';
    msg.textContent = e?.message || '비밀번호 변경에 실패했습니다.';
  }
});