"use strict"

const form = document.querySelector("form");
const submit = document.querySelector("input[type='submit']");

submit.addEventListener("click", () => {
    if (form.checkValidity()) {
        let counter = 0;

        const interval = setInterval(() => {
            submit.value = (counter++).toString();
            submit.disabled = true;

            if (counter === 100) {
                submit.classList.add("finished");
                submit.value = "✓";
                clearInterval(interval);
            }
        }, 25);
    }
});
