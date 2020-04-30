let oldpPasswordvalidated = false;
let retypePasswordvalidated = false;
$(function(){
    $("#upload_link").on('click', function(e){
        e.preventDefault();
        $("#upload:hidden").trigger('click');
    });
});
function changeAvatar() {
    localStorage.setItem("submit", "true");
    $('#uploadForm').submit();
}
$(document).ready(function (){
    $('#change-password').on('click', function(e){
        $('.profile-info').addClass('d-none');
        $('.change-password').removeClass('d-none');
    });
    $('#btn-back-profile').on('click', function(e){
        $('.change-password').addClass('d-none');
        $('.profile-info').removeClass('d-none');
    });

    $('#btnChangePassword').on('click', function () {
        changePassword();
    })
})
function checkConfirmationPassword(confirmationPassword) {
    if ($(confirmationPassword).val() !== $('#new-password').val()) {
        retypePasswordvalidated = false;
        $('#retype-failed').removeClass('d-none');
        $('#btnChangePassword').attr('disabled', true);
    } else {
        $('#retype-failed').addClass('d-none');
        retypePasswordvalidated = true;
        if(oldpPasswordvalidated){
            $('#btnChangePassword').removeAttr('disabled');
        }
    }
}
function checkMatchPassword(password) {
    let passwordDTO = {
        oldPassword : $(password).val()
    }
    $.ajax({
        type: 'POST',
        url: 'nera/api/email/password/check',
        data: JSON.stringify(passwordDTO),
        contentType: 'application/json',
        success: function (data) {
            if (data == 'failed') {
                oldpPasswordvalidated = false;
                $('#wrong-password').removeClass('d-none');
                $('#btnChangePassword').attr('disabled', true);
            } else {
                $('#wrong-password').addClass('d-none');
                oldpPasswordvalidated = true;
                if(retypePasswordvalidated){
                    $('#btnChangePassword').removeAttr('disabled');
                }
            }
        }
    })
}
function changePassword(){
    let oldPassword = $('#old-password').val();
    let newPassword = $('#new-password').val();
    let passwordDTO ={
        oldPassword: oldPassword,
        newPassword: newPassword
    };
    $.ajax({
        type: 'POST',
        url: 'nera/api/email/password/change',
        data: JSON.stringify(passwordDTO),
        contentType: "application/json",
        success: function () {
            Swal.fire({
                text:  "Successfully",
                type:  'success',
                timer: 2000,
                showConfirmButton: false
            });
        },
        error: function (xhr, data) {
            Swal.fire({
                text:  "Failed",
                type:  "error",
                timer: 2000,
                showConfirmButton: false
            });
        }

    })
}