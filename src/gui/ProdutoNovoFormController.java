package gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Strings;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Categoria;
import model.entities.Foto;
import model.entities.Produto;
import model.entities.Setor;
import model.services.CategoriaService;
import model.services.ProdutoService;
import model.services.SetorService;

public class ProdutoNovoFormController implements Initializable, DataChangeListener {

	private ProdutoService produtoService;

	private static Produto produto;

	private static Foto foto;

	private PrincipalFormController principalController;

	private byte[] bytes;

	private static File arquivo;

	private static String local = "";

	// Lista de ouvintes para receber alguma modifica��o
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtIdProduto;

	@FXML
	private TextField txtNome;

	@FXML
	private TextField txtQuantidade;

	@FXML
	private TextField txtEstoqueMinimo;

	@FXML
	private TextField txtEnderecoDaFoto;

	@FXML
	private ComboBox<String> comboBoxSetor;

	@FXML
	private ComboBox<String> comboBoxCategoria;

	@FXML
	private TextArea txtAreaDescricao;

	@FXML
	private Button btSalvarProduto;

	@FXML
	private Button btFotoProduto;

	@FXML
	private Button btVisualizarFoto;

	@FXML
	public void onBtSalvarProdutoAction(ActionEvent event) {

		setProduto(getFormData());
				
		if (produto != null) {

			produtoService.produtoNovoOuEditar(produto);
			notifyDataChangeListeners();
			Utils.fecharDialogAction();

		}

	}

	@FXML
	public void onBtFotoProdutoAction(ActionEvent event) {

		FileChooser chooser = new FileChooser();
		arquivo = null;

		chooser.getExtensionFilters().addAll(//
				new FileChooser.ExtensionFilter("All Files", "*.*"), new FileChooser.ExtensionFilter("JPG", "*.jpg"),
				new FileChooser.ExtensionFilter("PNG", "*.png"));

		chooser.setTitle("Escolher foto do produto");

		arquivo = chooser.showOpenDialog(new Stage());

		if (arquivo != null) {

			local = arquivo.getAbsolutePath();
			txtEnderecoDaFoto.setText(local);

			bytes = getByte();

			Foto fot = new Foto();
			fot.setLocal(local);
			fot.setFoto(bytes);

			setFoto(fot);

			Produto prod = new Produto();
			prod.setFoto(fot);

			setProduto(prod);

		} else {

			local = "";
			bytes = null;
			txtEnderecoDaFoto.setText(local);

		}

	}

	public static Foto getFoto() {
		return foto;
	}

	public static void setFoto(Foto foto) {
		ProdutoNovoFormController.foto = foto;
	}

	public static String getLocal() {
		return local;
	}

	public static void setLocal(String local) {
		ProdutoNovoFormController.local = local;
	}

	public static File getArquivo() {
		return arquivo;
	}

	public static void setArquivo(File arquivo) {
		ProdutoNovoFormController.arquivo = arquivo;
	}

	@FXML
	public void onBtVisualizarFotoAction(ActionEvent event) {

		if (!txtEnderecoDaFoto.getText().equals("")) {

			createVisualizarFotoDialogForm("/gui/VisualizarFotoView.fxml");

		} else {

			Alerts.showAlert("Visualizar foto", "Selecionar a foto do produto", "Primeiro selecione um imagem",
					AlertType.ERROR);

		}

	}

	private void createVisualizarFotoDialogForm(String absoluteName) {

		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			Main.setDialogScene(new Scene(pane));
			Stage produtoStage = new Stage();
			produtoStage.setTitle(Strings.getTitle());
			produtoStage.setScene(Main.getDialogScene());
			produtoStage.setResizable(false);
			produtoStage.initModality(Modality.APPLICATION_MODAL);
			produtoStage.initOwner(null);

			Image applicationIcon = new Image(getClass().getResourceAsStream("/imagens/bozo.jpg"));
			produtoStage.getIcons().add(applicationIcon);

			produtoStage.showAndWait();

		} catch (IOException e) {

			Alerts.showAlert("IO Exception", "Erro ao carregar a tela foto do produto", e.getMessage(),
					AlertType.ERROR);

		}

	}

	// Adiciona a lista um ouvinte, quando h� uma modifica��o
	public void subscribeDataChangeListener(DataChangeListener listener) {

		dataChangeListeners.add(listener);

	}

	// Fun��o que faz a atualiza��o da tabela
	private void notifyDataChangeListeners() {

		for (DataChangeListener listener : dataChangeListeners) {
			
			listener.onDataChanged();
			
		}

	}

	public void setProdutoService(ProdutoService produtoService) {
		
		this.produtoService = produtoService;
		
	}

	public void setProduto(Produto produto) {

		ProdutoNovoFormController.produto = produto;

	}

	private List<String> listaSetor() {

		SetorService setorService = new SetorService();
		List<String> listaSetor = new ArrayList<>();

		for (Setor setor : setorService.findAllNome()) {

			listaSetor.add(setor.getNome());
		}

		return listaSetor;

	}

	private List<String> listaCategoria() {

		CategoriaService categoriaService = new CategoriaService();
		List<String> listaCategoria = new ArrayList<>();

		for (Categoria categoria : categoriaService.findAllNome()) {

			listaCategoria.add(categoria.getNome());
		}

		return listaCategoria;

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		initializeNodes();

	}

	private void initializeNodes() {

		comboBoxSetor.setItems(FXCollections.observableArrayList(listaSetor()));
		comboBoxCategoria.setItems(FXCollections.observableArrayList(listaCategoria()));

		produtoService = new ProdutoService();
		produto = new Produto();
		foto = new Foto();
		local = new String();

		Constraints.setTextFieldInteger(txtQuantidade);
		Constraints.setTextFieldInteger(txtEstoqueMinimo);

		principalController = new PrincipalFormController();
	}

	private Produto getFormData() {

		Produto produto = new Produto();
		Foto foto = new Foto();

		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {

			Alerts.showAlert("Novo Produto", "Campo obrigat�rio", "Digite o nome do Produto", AlertType.INFORMATION);

			txtNome.requestFocus();

			produto = null;

		} else if (txtQuantidade.getText() == null || txtQuantidade.getText().trim().equals("")) {

			Alerts.showAlert("Novo Produto", "Campo obrigat�rio", "Digite a quantidade inicial do produto",
					AlertType.INFORMATION);

			txtQuantidade.requestFocus();

			produto = null;

		} else if (txtEstoqueMinimo.getText() == null || txtEstoqueMinimo.getText().trim().equals("")) {

			Alerts.showAlert("Novo Produto", "Campo obrigat�rio", "Digite o estoque m�nimo", AlertType.INFORMATION);

			txtAreaDescricao.requestFocus();

			produto = null;

		} else if (comboBoxSetor.getSelectionModel().getSelectedItem() == null) {

			Alerts.showAlert("Novo Produto", "Campos obrigat�rio", "Selecione o setor", AlertType.INFORMATION);

			comboBoxSetor.requestFocus();

			produto = null;

		} else if (comboBoxCategoria.getSelectionModel().getSelectedItem() == null) {

			Alerts.showAlert("Novo Produto", "Campo obrigat�rio", "Selecione a categoria", AlertType.INFORMATION);

			comboBoxCategoria.requestFocus();

			produto = null;

		} else if (txtAreaDescricao.getText() == null || txtAreaDescricao.getText().trim().equals("")) {

			Alerts.showAlert("Novo Produto", "Campo obrigat�rio", "Digite a descri��o do produto", AlertType.INFORMATION);

			txtAreaDescricao.requestFocus();

			produto = null;

		} else {

			foto.setLocal(local);
			foto.setFoto(bytes);

			produto.setNome(txtNome.getText());
			produto.setQuantidade(Integer.valueOf(txtQuantidade.getText()));
			produto.setEstoqueMinimo(Integer.valueOf(txtEstoqueMinimo.getText()));
			produto.setSetor(String.valueOf(comboBoxSetor.getSelectionModel().getSelectedItem()));
			produto.setCategoria(String.valueOf(comboBoxCategoria.getSelectionModel().getSelectedItem()));
			produto.setDescricao(txtAreaDescricao.getText());
			produto.setFoto(foto);

		}

		return produto;

	}

	@Override
	public void onDataChanged() {

		principalController.updateTableView();

	}

	public TextField getTxtEnderecoDaFoto() {

		return txtEnderecoDaFoto;

	}

	public void setTxtEnderecoDaFoto(TextField txtEnderecoDaFoto) {

		this.txtEnderecoDaFoto = txtEnderecoDaFoto;

	}

	public byte[] getByte() {

		InputStream converter = null;
		bytes = new byte[(int) local.length()];

		int offset = 0;
		int numRead = 0;

		try {

			converter = new FileInputStream(ProdutoNovoFormController.local);

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		}

		try {

			while (offset < bytes.length && (numRead = converter.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}

		} catch (IOException e) {

			e.printStackTrace();

		}

		return bytes;
	}

}
