document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("password").addEventListener("input",passwordCompare);
    document.getElementById("password2").addEventListener("input",passwordCompare);
})

const password=document.getElementById("password");
const password2=document.getElementById("password2");
const form=document.getElementById("passwordreset-form");

function passwordCompare(){
    if(password.value===password2.value&& password.value.length >= 8&&password.value.length<=12){
        form.disabled=false;
    }else{
        form.disabled=true;
    }
}