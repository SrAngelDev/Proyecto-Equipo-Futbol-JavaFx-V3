package srangeldev.theme

import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Region

/**
 * Class to manage application themes
 */
class Theme {
    companion object {
        // Dark theme colors (from splash screen and login)
        private const val DARK_BACKGROUND_GRADIENT = "-fx-background-color: linear-gradient(to bottom, #1E1E1E, #2A2A2A);"
        private const val DARK_OUTER_BACKGROUND = "-fx-background-color: linear-gradient(to bottom, #121212, #1A1A1A);"
        private const val DARK_INNER_BACKGROUND = "-fx-background-color: #2A2A2A;"
        private const val DARK_FIELD_BACKGROUND = "-fx-background-color: #2A2A2A; -fx-text-fill: white; -fx-prompt-text-fill: #AAAAAA; -fx-background-radius: 5; -fx-border-color: #3A3A3A; -fx-border-radius: 5;"
        private const val DARK_TEXT_COLOR = "-fx-text-fill: white;"
        private const val DARK_SECONDARY_TEXT_COLOR = "-fx-text-fill: #AAAAAA;"
        private const val DARK_ACCENT_COLOR = "-fx-background-color: #4CAF50; -fx-text-fill: white;"
        private const val DARK_TABLE_STYLE = "-fx-background-color: #2A2A2A; -fx-text-fill: white; -fx-background-radius: 5; -fx-border-color: #3A3A3A; -fx-border-radius: 5;"
        
        // Light theme colors (current user/admin views)
        private const val LIGHT_BACKGROUND = "-fx-background-color: #f0f0f0;"
        private const val LIGHT_INNER_BACKGROUND = "-fx-background-color: white;"
        private const val LIGHT_FIELD_BACKGROUND = "-fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #cccccc; -fx-border-width: 1;"
        private const val LIGHT_TEXT_COLOR = "-fx-text-fill: #333333;"
        private const val LIGHT_ACCENT_COLOR = "-fx-background-color: #4CAF50; -fx-text-fill: white;"
        private const val LIGHT_TABLE_STYLE = "-fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #cccccc; -fx-border-width: 1;"
        
        // Current theme state
        private var isDarkTheme = false
        
        /**
         * Toggle between light and dark themes
         */
        fun toggleTheme(scene: Scene) {
            isDarkTheme = !isDarkTheme
            applyTheme(scene)
        }
        
        /**
         * Apply the current theme to the scene
         */
        fun applyTheme(scene: Scene) {
            val root = scene.root
            if (isDarkTheme) {
                applyDarkTheme(root)
            } else {
                applyLightTheme(root)
            }
        }
        
        /**
         * Apply dark theme to a node and its children
         */
        private fun applyDarkTheme(node: javafx.scene.Node) {
            when (node) {
                is AnchorPane -> {
                    node.style = DARK_OUTER_BACKGROUND
                }
                is javafx.scene.control.TableView<*> -> {
                    node.style = DARK_TABLE_STYLE
                }
                is javafx.scene.control.TextField, 
                is javafx.scene.control.PasswordField,
                is javafx.scene.control.ComboBox<*>,
                is javafx.scene.control.DatePicker -> {
                    node.style = DARK_FIELD_BACKGROUND
                }
                is javafx.scene.control.Label,
                is javafx.scene.text.Text -> {
                    node.style = DARK_TEXT_COLOR
                }
                is Button -> {
                    if (node.style.contains("#f44336")) {
                        // Keep red buttons for cancel/delete
                        node.style = "-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 5;"
                    } else {
                        node.style = DARK_ACCENT_COLOR + " -fx-background-radius: 5;"
                    }
                }
                is javafx.scene.control.MenuBar -> {
                    node.style = DARK_ACCENT_COLOR
                }
                is javafx.scene.layout.VBox,
                is javafx.scene.layout.HBox,
                is javafx.scene.layout.GridPane -> {
                    if (node.style.contains("-fx-background-color")) {
                        node.style = DARK_INNER_BACKGROUND
                    }
                }
            }
            
            // Apply theme to children
            if (node is javafx.scene.Parent) {
                for (child in node.childrenUnmodifiable) {
                    applyDarkTheme(child)
                }
            }
        }
        
        /**
         * Apply light theme to a node and its children
         */
        private fun applyLightTheme(node: javafx.scene.Node) {
            when (node) {
                is AnchorPane -> {
                    node.style = LIGHT_BACKGROUND
                }
                is javafx.scene.control.TableView<*> -> {
                    node.style = LIGHT_TABLE_STYLE
                }
                is javafx.scene.control.TextField,
                is javafx.scene.control.PasswordField,
                is javafx.scene.control.ComboBox<*>,
                is javafx.scene.control.DatePicker -> {
                    node.style = LIGHT_FIELD_BACKGROUND
                }
                is javafx.scene.control.Label,
                is javafx.scene.text.Text -> {
                    node.style = LIGHT_TEXT_COLOR
                }
                is Button -> {
                    if (node.style.contains("#f44336")) {
                        // Keep red buttons for cancel/delete
                        node.style = "-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 5;"
                    } else {
                        node.style = LIGHT_ACCENT_COLOR + " -fx-background-radius: 5;"
                    }
                }
                is javafx.scene.control.MenuBar -> {
                    node.style = LIGHT_ACCENT_COLOR
                }
                is javafx.scene.layout.VBox,
                is javafx.scene.layout.HBox,
                is javafx.scene.layout.GridPane -> {
                    if (node.style.contains("-fx-background-color")) {
                        node.style = LIGHT_INNER_BACKGROUND
                    }
                }
            }
            
            // Apply theme to children
            if (node is javafx.scene.Parent) {
                for (child in node.childrenUnmodifiable) {
                    applyLightTheme(child)
                }
            }
        }
        
        /**
         * Get current theme state
         */
        fun isDarkTheme(): Boolean {
            return isDarkTheme
        }
        
        /**
         * Set theme state
         */
        fun setDarkTheme(dark: Boolean) {
            isDarkTheme = dark
        }
    }
}