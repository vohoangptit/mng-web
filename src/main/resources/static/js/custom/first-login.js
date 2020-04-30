const login = $('#kt_login');


let displayChangeForm = function() {

    $('.kt-change-password').addClass('d-none');
    $('.kt-change-success').removeClass('d-none');
    KTUtil.animateClass(login.find('.kt-change-success')[0], 'flipInX animated');

}
function changePasswordFirstLogin(){
    let newPassword = $('#new_password').val();
    let passwordDTO ={
        newPassword: newPassword
    };
    $.ajax({
        type: 'POST',
        url: '../nera/api/email/password/first-login',
        data: JSON.stringify(passwordDTO),
        contentType: "application/json",
        success: function () {
            displayChangeForm();
        }
    })
}
function checkEmptyPassword(password) {
    if ($('#new_password').val() === "") {
        $('#empty-password').removeClass('d-none');
    } else {
        $('#empty-password').addClass('d-none');
    }
}
function checkRetypePassword(confirmationPassword) {

    if ($(confirmationPassword).val() !== $('#new_password').val()) {
        $('#retype-failed').removeClass('d-none');
        $('#change-first-login-submit').attr('disabled', true);
    } else {
        $('#retype-failed').addClass('d-none');
        $('#change-first-login-submit').removeAttr('disabled');
    }
}
// Class Initializations
$(document).ready(function() {

    $('#change-first-login-submit').click(function(e) {
        changePasswordFirstLogin();
    });
});