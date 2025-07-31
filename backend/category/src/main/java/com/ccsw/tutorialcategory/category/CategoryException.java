package com.ccsw.tutorialcategory.category;

public class CategoryException extends Exception {
    public static String CATEGORY_ID_NOT_FOUND = "CATEGORY_ID_NOT_FOUND";
    public static String CATEGORY_ID_NOT_FOUND_EXTENDED = "No se puede borrar la categoría debido a que el ID proporcionado no existe en la base de datos.";

    public static String CATEGORY_HAS_GAMES = "CATEGORY_HAS_GAMES";
    public static String CATEGORY_HAS_GAMES_EXTENDED = "No se puede borrar la categoría debido a que tiene juegos registrados a su nombre.";

    public String extendedMessage;

    public CategoryException(String error, String extendedMessage) {
        super(error);
        this.extendedMessage = extendedMessage;
    }

    public String getExtendedMessage() {
        return extendedMessage;
    }
}
