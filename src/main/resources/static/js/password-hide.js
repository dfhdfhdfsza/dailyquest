document.addEventListener("DOMContentLoaded",function(){
   document.querySelectorAll(".fa-solid.fa-eye").forEach(icon => {
       icon.addEventListener("click", passwordHide);
   });
})

function passwordHide(){
    const icon = document.querySelector(".fa-solid.fa-eye");
    const inputs = document.querySelectorAll(".pw-input");

    const type = inputs[0].type;

    if(type === "password"){
        // 모든 input 태그를 text로 변경
        inputs.forEach(el => el.type = "text");

        // 모든 fa-eye 아이콘 색 변경
        document.querySelectorAll(".fa-solid.fa-eye").forEach(icon => {
            icon.style.color = "black";
        })
    }else{
        inputs.forEach(el => el.type = "password");

                // 모든 fa-eye 아이콘 색 변경
        document.querySelectorAll(".fa-solid.fa-eye").forEach(icon => {
            icon.style.color = "#888";
        })
    }

}