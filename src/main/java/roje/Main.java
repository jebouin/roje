package roje;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import roje.model.Character;
import roje.model.Comics;
import roje.model.ComicsDAO;
import roje.model.DB;
import roje.view.CharacterCardController;
import roje.view.ComicCardController;
import roje.view.LibraryController;
import roje.view.RecommendationsController;

public class Main extends Application {
	public static Main instance;
	private Stage primaryStage;
	private BorderPane rootLayout;
	private LibraryController libraryController;
	private RecommendationsController recommendationsController;

	public LibraryController getLibraryController() {
		return libraryController;
	}

	public void setLibraryController(LibraryController libraryController) {
		this.libraryController = libraryController;
	}

	public RecommendationsController getRecommendationsController() {
		return recommendationsController;
	}

	public void setRecommendationsController(RecommendationsController recommendationsController) {
		this.recommendationsController = recommendationsController;
	}

	public void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showCharacterCard(final Character character) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("view/CharacterCardView.fxml"));
		ScrollPane pane = (ScrollPane) loader.load();
		loader.<CharacterCardController> getController().setCharacter(character);
		Stage stage = new Stage();
		stage.setTitle(character.getName());
		stage.setScene(new Scene(pane, 1400, 900));
		stage.show();
	}

	public void showComicCard(final Comics comic) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("view/ComicCardView.fxml"));
		ScrollPane pane = (ScrollPane) loader.load();
		loader.<ComicCardController> getController().setComic(comic);
		Stage stage = new Stage();
		stage.setTitle(comic.getTitle());
		stage.setScene(new Scene(pane, 1400, 900));
		stage.show();
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public BorderPane getRootLayout() {
		return rootLayout;
	}

	@Override
	public void start(Stage primaryStage) {
		Main.instance = this;
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Roje Comics Manager");
		initRootLayout();
	}

	public static void main(String[] args) throws Exception {
		DB.createDB();
		ComicsDAO.init();
		launch(args);
		ComicsDAO.close();
	}
}