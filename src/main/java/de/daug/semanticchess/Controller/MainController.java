package de.daug.semanticchess.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Main controller that maps pages to the HTML files.
 */
@Controller
public class MainController {

	/**
	 * maps "/" to index.html
	 * @return index.html
	 */
	@GetMapping("/")
	public String home() {
		return "index";

	}
	
	
	/**
	 * maps "/game" to game.html
	 * @return index.html
	 */
	@GetMapping("/gameView")
	public String game() {
		return "game";

	}
	
	/**
	 * maps "/search" to search.html
	 * @return index.html
	 */
	@GetMapping("/searchView")
	public String search() {
		return "search";

	}
}