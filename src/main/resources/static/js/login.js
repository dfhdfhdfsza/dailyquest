document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("loginForm");

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const loginId = document.getElementById("id-input").value.trim();
        const password = document.getElementById("pw-input").value.trim();

        if (!loginId || !password) {
            alert("아이디와 비밀번호를 모두 입력해주세요.");
            return;
        }

        try {
            const response = await fetch("/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ loginId, password })
            });

            if (!response.ok) {
                const errorText = await response.text();
                alert(`로그인 실패: ${errorText}`);
                return;
            }

            const { token } = await response.json();

            //  JWT 저장 (로컬스토리지)
            localStorage.setItem("token", token);

            alert('로그인 성공');

            //  인덱스 페이지로 이동
            window.location.href = "/";
        } catch (err) {
            console.error("로그인 중 오류 발생:", err);
            alert("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }
    });
});