package model.services;

import java.util.List;
import java.util.Optional;

import gui.util.Alerts;
import javafx.scene.control.ButtonType;
import model.dao.CategoriaDao;
import model.dao.DaoFactory;
import model.entities.Categoria;

public class CategoriaService {

	Categoria categoria = new Categoria();

	private CategoriaDao dao = DaoFactory.createCategoriaDao();

	public void categoriaNovoOuEditar(Categoria categoria) {

		if (categoria.getIdCategoria() == null) {

			Optional<ButtonType> result = Alerts.showConfirmation("Confirma��o", "Voc� deseja salvar a categoria " + categoria.getNome() + " ?");

			if (result.get() == ButtonType.OK) {

				dao.insert(categoria);

			}

		} else {

			Optional<ButtonType> result = Alerts.showConfirmation("Confirma��o", "Voc� deseja editar a categoria " + categoria.getNome() + " ?");

			if (result.get() == ButtonType.OK) {

				dao.update(categoria);

			}

		}
	}

	public void remove(Categoria categoria) {
		dao.deleteById(categoria.getIdCategoria());
	}

	public List<Categoria> findAllId() {
		return dao.findAllId();
	}
	
	public List<Categoria> findAllNome() {
		return dao.findAllNome();
	}

}
