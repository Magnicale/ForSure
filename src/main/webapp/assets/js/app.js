(() => {
    const timers = document.querySelectorAll(".countdown[data-unlock-at]");
    if (!timers.length) return;

    const formatRemaining = (seconds) => {
        const days = Math.floor(seconds / 86400);
        const hours = Math.floor((seconds % 86400) / 3600);
        const minutes = Math.floor((seconds % 3600) / 60);
        const rest = seconds % 60;
        if (days > 0) return `${days} дн. ${hours} ч. ${minutes} мин.`;
        if (hours > 0) return `${hours} ч. ${minutes} мин. ${rest} сек.`;
        if (minutes > 0) return `${minutes} мин. ${rest} сек.`;
        return `${rest} сек.`;
    };

    const revealCapsule = async (card, timer) => {
        if (!card || card.dataset.revealed === "true" || card.dataset.loading === "true") return;
        const lastAttempt = Number(card.dataset.lastAttempt || 0);
        if (Date.now() - lastAttempt < 5000) return;
        card.dataset.loading = "true";
        card.dataset.lastAttempt = String(Date.now());
        try {
            const response = await fetch(card.dataset.apiUrl, { headers: { Accept: "application/json" } });
            if (!response.ok) throw new Error("Ответ сервера недоступен");
            const data = await response.json();
            if (!data.unlocked) return;

            const content = card.querySelector(".sealed-content");
            const image = document.createElement("img");
            image.className = "capsule-photo";
            image.src = data.mediaUrl;
            image.alt = "Фото капсулы";
            image.loading = "lazy";

            const note = document.createElement("p");
            note.className = "capsule-note";
            note.textContent = data.note;
            content.replaceChildren(image, note);

            if (Array.isArray(data.tags) && data.tags.length) {
                const list = document.createElement("ul");
                list.className = "tag-list";
                for (const label of data.tags) {
                    const item = document.createElement("li");
                    item.textContent = label;
                    list.appendChild(item);
                }
                content.appendChild(list);
            }
            content.classList.remove("is-hidden");
            card.classList.remove("is-sealed");
            card.classList.add("is-open");
            card.dataset.revealed = "true";
            timer.textContent = "Капсула открыта";
        } catch (error) {
            timer.textContent = "Проверяем открытие…";
        } finally {
            card.dataset.loading = "false";
        }
    };

    const tick = () => {
        const now = Date.now();
        for (const timer of timers) {
            const target = Number(timer.dataset.unlockAt);
            const card = timer.closest(".capsule-card");
            if (target <= now) {
                timer.textContent = "Капсула открыта";
                if (card && card.dataset.revealed !== "true") revealCapsule(card, timer);
            } else {
                timer.textContent = `Откроется через ${formatRemaining(Math.ceil((target - now) / 1000))}`;
            }
        }
    };

    tick();
    window.setInterval(tick, 1000);
})();
