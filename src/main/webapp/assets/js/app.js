(function () {
  function fallbackCopy(text) {
    var ta = document.createElement("textarea");
    ta.value = text;
    ta.setAttribute("readonly", "");
    ta.setAttribute("aria-hidden", "true");
    ta.style.position = "fixed";
    ta.style.top = "0";
    ta.style.left = "0";
    ta.style.width = "2em";
    ta.style.height = "2em";
    ta.style.padding = "0";
    ta.style.border = "none";
    ta.style.outline = "none";
    ta.style.boxShadow = "none";
    ta.style.background = "transparent";
    document.body.appendChild(ta);
    ta.focus();
    ta.select();
    ta.setSelectionRange(0, text.length);
    var ok = false;
    try {
      ok = document.execCommand("copy");
    } catch (ignored) {
      ok = false;
    }
    document.body.removeChild(ta);
    return ok;
  }

  function copyText(text) {
    if (window.isSecureContext && navigator.clipboard && navigator.clipboard.writeText) {
      return navigator.clipboard.writeText(text).catch(function () {
        if (!fallbackCopy(text)) {
          throw new Error("copy failed");
        }
      });
    }
    if (fallbackCopy(text)) {
      return Promise.resolve();
    }
    return Promise.reject(new Error("copy failed"));
  }

  function flashCopied(btn) {
    var original = btn.getAttribute("data-label") || btn.textContent;
    btn.setAttribute("data-label", original);
    btn.textContent = "Copied";
    window.setTimeout(function () {
      btn.textContent = original;
    }, 1600);
  }

  document.addEventListener("click", function (event) {
    var btn = event.target.closest("[data-copy]");
    if (!btn) {
      return;
    }
    event.preventDefault();
    var box = btn.parentElement ? btn.parentElement.querySelector(".link-box") : null;
    var text = (box && box.textContent ? box.textContent : btn.getAttribute("data-copy") || "").trim();
    if (!text) {
      return;
    }
    copyText(text).then(function () {
      flashCopied(btn);
    }).catch(function () {
      window.prompt("Copy this link", text);
    });
  });
})();
