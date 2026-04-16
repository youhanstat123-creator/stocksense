const toggleBtn = document.getElementById("themeToggle");
const root = document.documentElement;

// 페이지 로드 시 저장된 테마 적용
const savedTheme = localStorage.getItem("theme");
if (savedTheme) {
    root.setAttribute("data-theme", savedTheme);
}

toggleBtn?.addEventListener("click", () => {
    const isDark = root.getAttribute("data-theme") === "dark";

    if (isDark) {
        root.removeAttribute("data-theme");
        localStorage.setItem("theme", "light");
        toggleBtn.innerText = "🌙 다크모드";
    } else {
        root.setAttribute("data-theme", "dark");
        localStorage.setItem("theme", "dark");
        toggleBtn.innerText = "☀️ 라이트모드";
    }
});
