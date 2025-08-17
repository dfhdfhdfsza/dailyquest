document.getElementById("logoutBtn").addEventListener("click", async (e) => {
  try {
    e.preventDefault(); // 페이지 이동 막기
    await api.post("/logout"); // 서버가 refresh 쿠키 무효화
  } catch (_) {
    // 서버가 204/200가 아니어도 무시 가능
  } finally {
    localStorage.removeItem("accessToken"); // 클라이언트 토큰 제거
    location.replace("/"); // 페이지 이동은 여기서
  }
});



