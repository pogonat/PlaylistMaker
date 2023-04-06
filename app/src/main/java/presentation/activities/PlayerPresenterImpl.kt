package presentation.activities

class PlayerPresenterImpl(
    // приходят UseCase ы и переменная View
    private val view: PlayerView //ссылка на активити, просто закрыта интерфейсом
): PlayerPresenter {
    // вызываем методы активити у val view
}