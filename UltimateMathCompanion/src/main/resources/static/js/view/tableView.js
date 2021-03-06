
class TableView {

    _tableRows = document.querySelector('.expressions-table').querySelectorAll('tr');

    constructor() {
        this._selectedId = this._tableRows[0].id;
        this._selectRow(this._selectedId);
    }

    get getSelectedId() {
        return this._selectedId;
    }

    addHandlerTableRow() {
        this._tableRows.forEach(row => row.addEventListener('click', this._handleTableRow.bind(this)));
    }

    _handleTableRow(e) {
        this._selectRow(e.target.closest('.exprRow').id);
    }

    _selectRow(id) {
        document.getElementById(this._selectedId).classList.remove('selected-row');
        document.getElementById(id).classList.add('selected-row');
        this._selectedId = id;
    }

}
const tableView = new TableView();
export default tableView;


