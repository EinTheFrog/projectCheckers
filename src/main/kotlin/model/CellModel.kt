package model

class CellModel(val x: Int, val y: Int) {
    private var _checker: Checker? = null
    public var checker: Checker?
        get() = _checker
        private  set(value: Checker?) {
            _checker = value
        }
}