(function ($) {
  function pct(n) {
    return Math.max(0, Math.min(100, n));
  }

  function applyBox($box, x, y, w) {
    $box.css({
      left: x + "%",
      top: y + "%",
      width: w + "%"
    });
  }

  function syncInputs($box) {
    var key = $box.data("key");
    $("input[name='" + key + "_x']").val($box.data("x"));
    $("input[name='" + key + "_y']").val($box.data("y"));
    $("input[name='" + key + "_w']").val($box.data("w"));
  }

  function restyle($box) {
    var key = $box.data("key");
    var size = parseInt($("input[name='" + key + "_size']").val(), 10) || 24;
    var color = $("input[name='" + key + "_color']").val() || "#1a2744";
    var bold = $("input[name='" + key + "_bold']").is(":checked");
    var align = $("select[name='" + key + "_align']").val() || "center";
    var stageW = $(".align-stage").width();
    var nativeW = parseInt($(".align-stage").data("native-width"), 10) || 1920;
    var scaled = size * (stageW / nativeW);
    $box.css({
      color: color,
      fontWeight: bold ? 700 : 500,
      textAlign: align,
      fontSize: scaled + "px"
    });
  }

  $(function () {
    var $stage = $(".align-stage");
    if (!$stage.length) return;

    $(".field-box").each(function () {
      var $box = $(this);
      applyBox($box, $box.data("x"), $box.data("y"), $box.data("w"));
      restyle($box);
    });

    $(window).on("resize", function () {
      $(".field-box").each(function () { restyle($(this)); });
    });

    $(".field-box").on("mousedown", function (e) {
      if ($(e.target).hasClass("handle")) return;
      var $box = $(this);
      $(".field-box").removeClass("active");
      $box.addClass("active");
      var startX = e.pageX;
      var startY = e.pageY;
      var origX = parseFloat($box.data("x"));
      var origY = parseFloat($box.data("y"));
      var stageW = $stage.width();
      var stageH = $stage.height();

      function move(ev) {
        var dx = ((ev.pageX - startX) / stageW) * 100;
        var dy = ((ev.pageY - startY) / stageH) * 100;
        var x = pct(origX + dx);
        var y = pct(origY + dy);
        $box.data("x", x.toFixed(2));
        $box.data("y", y.toFixed(2));
        applyBox($box, x, y, parseFloat($box.data("w")));
        syncInputs($box);
      }
      function up() {
        $(window).off("mousemove", move).off("mouseup", up);
      }
      $(window).on("mousemove", move).on("mouseup", up);
      e.preventDefault();
    });

    $(".field-box .handle").on("mousedown", function (e) {
      var $box = $(this).closest(".field-box");
      var startX = e.pageX;
      var origW = parseFloat($box.data("w"));
      var stageW = $stage.width();
      function move(ev) {
        var dw = ((ev.pageX - startX) / stageW) * 100;
        var w = Math.max(12, Math.min(100 - parseFloat($box.data("x")), origW + dw));
        $box.data("w", w.toFixed(2));
        applyBox($box, parseFloat($box.data("x")), parseFloat($box.data("y")), w);
        syncInputs($box);
      }
      function up() {
        $(window).off("mousemove", move).off("mouseup", up);
      }
      $(window).on("mousemove", move).on("mouseup", up);
      e.preventDefault();
      e.stopPropagation();
    });

    $(".align-controls input, .align-controls select").on("input change", function () {
      var key = $(this).closest("[data-field]").data("field");
      var $box = $(".field-box[data-key='" + key + "']");
      $box.data("x", $("input[name='" + key + "_x']").val());
      $box.data("y", $("input[name='" + key + "_y']").val());
      $box.data("w", $("input[name='" + key + "_w']").val());
      applyBox($box, parseFloat($box.data("x")), parseFloat($box.data("y")), parseFloat($box.data("w")));
      restyle($box);
    });
  });
})(jQuery);
