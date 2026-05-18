// Confirma exclusoes antes de enviar formularios de DELETE/POST.
function confirmarExclusao(item) {
    return window.confirm('Tem certeza que deseja excluir ' + item + '? Esta ação não pode ser desfeita.');
}

// Inicializa comportamentos simples depois que a pagina terminou de carregar.
document.addEventListener('DOMContentLoaded', function () {
    // Mensagens de sucesso/erro desaparecem sozinhas apos alguns segundos.
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            alert.style.transition = 'opacity 0.4s';
            alert.style.opacity = '0';
            setTimeout(function () {
                alert.remove();
            }, 400);
        }, 5000);
    });

    // Na tela de prova, atualiza a quantidade de questoes marcadas.
    const picker = document.getElementById('questionPicker');
    if (picker) {
        const checkboxes = picker.querySelectorAll('input[type="checkbox"]');
        checkboxes.forEach(function (cb) {
            cb.addEventListener('change', atualizarContadorQuestoes);
        });
        atualizarContadorQuestoes();
    }
});

// Atualiza ou cria o contador visual de questoes selecionadas.
function atualizarContadorQuestoes() {
    const picker = document.getElementById('questionPicker');
    if (!picker) return;
    const selecionadas = picker.querySelectorAll('input[type="checkbox"]:checked').length;
    let contador = document.getElementById('contadorQuestoes');
    if (!contador) {
        contador = document.createElement('p');
        contador.id = 'contadorQuestoes';
        contador.className = 'hint';
        picker.parentElement.insertBefore(contador, picker);
    }
    contador.textContent = selecionadas + ' questão(ões) selecionada(s)';
}
