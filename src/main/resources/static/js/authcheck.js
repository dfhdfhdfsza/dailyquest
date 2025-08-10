document.addEventListener("DOMContentLoaded",function(){

    const token=localStorage.getItem("token");

    const loginBtn=document.getElementById("loginBtn");
    const logoutBtn=document.getElementById("logoutBtn");
    const signupBtn=document.getElementById("signupBtn");

    if(token){
        loginBtn.style.display="none";
        signupBtn.style.display="none";
        logoutBtn.style.display="inline-block";
    }else{
        loginBtn.style.display="inline-block";
        signupBtn.style.display="inline-block";
        logoutBtn.style.display="none";
    }

})