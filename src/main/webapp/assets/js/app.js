(function ($) {
  $(function () {
    $("[data-copy]").on("click", function () {
      var text = $(this).attr("data-copy");
      var btn = $(this);
      if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(text).then(function () {
          var original = btn.text();
          btn.text("Copied");
          setTimeout(function () { btn.text(original); }, 1600);
        });
      }
    });
  });
})(jQuery);
