(function ($) {
  var EMAIL = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(?:\.[A-Za-z0-9-]+)*\.[A-Za-z]{2,63}$/;

  function validPhone(raw) {
    if (!raw) return true;
    var compact = raw.replace(/[\s().-]/g, "");
    if (compact.charAt(0) === "+") compact = compact.slice(1);
    if (!/^\d{10,15}$/.test(compact)) return false;
    if (compact.length === 12 && compact.indexOf("91") === 0) compact = compact.slice(2);
    else if (compact.length === 11 && compact.charAt(0) === "0") compact = compact.slice(1);
    if (compact.length === 10) return /^[6-9]/.test(compact);
    return compact.length >= 10 && compact.length <= 15;
  }

  function showError(msg) {
    var $el = $("#clientError");
    $el.removeClass("d-none").text(msg);
    $("#formError").addClass("d-none");
  }

  $(function () {
    var $form = $("#attendeeForm");
    if (!$form.length) return;
    $form.on("submit", function (e) {
      var name = $.trim($("#fullName").val());
      var email = $.trim($("#email").val());
      var phone = $.trim($("#phone").val());
      var institute = $.trim($("#institute").val());
      var speciality = $.trim($("#speciality").val());
      var designation = $.trim($("#designation").val());
      if (!name) { e.preventDefault(); showError("Enter your full name as it should appear on the certificate."); return; }
      if (!EMAIL.test(email) || email.indexOf("..") >= 0) {
        e.preventDefault();
        showError("Enter a valid email address (for example name@institute.edu).");
        return;
      }
      if (!validPhone(phone)) {
        e.preventDefault();
        showError("Enter a valid 10-digit mobile number, or leave it blank.");
        return;
      }
      if (!institute) { e.preventDefault(); showError("Enter your institute name."); return; }
      if (!speciality) { e.preventDefault(); showError("Enter your speciality."); return; }
      if (!designation) { e.preventDefault(); showError("Enter your designation."); return; }
    });
  });
})(jQuery);
