document.addEventListener("DOMContentLoaded", async () => {
  const url = new URL(window.location.href);
  const code = url.searchParams.get("code");

  if (!code) {
    alert("로그인 코드가 없습니다.");
    window.location.replace("/login?error=social");
    return;
  }

  try {
    // 네 스타일대로 fetch + JSON 바디
    const response = await fetch("/api/auth/social/exchange", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ code })
    });

    if (!response.ok) {
      const errorText = await response.text().catch(() => "교환 실패");
      console.error("소셜 교환 실패:", errorText);
      alert("소셜 로그인 교환에 실패했습니다.");
      window.location.replace("/login?error=social");
      return;
    }

    // 응답 형태 변화 대응: {accessToken} 또는 {data:{accessToken}}
    const data = await response.json().catch(() => ({}));
    const accessToken =
      data?.accessToken ||
      data?.data?.accessToken ||
      null;

    if (!accessToken) {
      alert("액세스 토큰을 받지 못했습니다.");
      window.location.replace("/login?error=social");
      return;
    }

    // 네 login.js 스타일: 기본은 localStorage에 저장, sessionStorage는 비움
    // (자동로그인 체크박스가 이 페이지엔 없으니 기본을 '영속'으로 둡니다.)
    localStorage.setItem("accessToken", accessToken);
    sessionStorage.removeItem("accessToken");

    alert("로그인 성공");
    window.location.href = "/";
  } catch (err) {
    console.error("교환 중 오류:", err);
    alert("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    window.location.replace("/login?error=social");
  }
});