const logout=document.getElementById("logoutBtn");

logout.addEventListener("click",function(e){
    localStorage.removeItem("token");   //토큰 삭제
    window.location.href = "/";    //메인페이지로 이동
})