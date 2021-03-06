package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import dao.BookDao;
import model.Book;


public class BookController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static String INSERT_OR_EDIT = "/book.jsp";
	public static String LIST_BOOK = "/dashboard-book.jsp";
	public static String BOOK_DISPLAY= "/display-books.jsp";
	public static String BOOK_DETAIL= "/display-book-detail.jsp";
	private BookDao dao;

	public BookController() {
		super();
		dao = new BookDao();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String forward = "";
		String action = request.getParameter("action");
		// System.out.println("GETTING ALL THE THINGS!!!: " + action);
		if (action.equalsIgnoreCase("delete")) {
			int bookId = Integer.parseInt(request.getParameter("bookId"));
			dao.deleteBook(bookId);
			forward = LIST_BOOK;
			request.setAttribute("books", dao.getAllBooks());
		}
		else if (action.equalsIgnoreCase("edit")) {
			forward = INSERT_OR_EDIT;
			int bookId = Integer.parseInt(request.getParameter("bookId"));
			Book book = dao.getBookById(bookId);
			request.setAttribute("book", book);
		}
		else if (action.equalsIgnoreCase("listBook")){
			forward = LIST_BOOK;
			request.setAttribute("books", dao.getAllBooks());
		}
		else if (action.equalsIgnoreCase("bookDisplay")){
			forward = BOOK_DISPLAY;
			String category = request.getParameter("category");
			// System.out.println("category = " + category);
			if (category.equalsIgnoreCase("all")) {
				request.setAttribute("books", dao.getAllBooks());
			} else if (category.equalsIgnoreCase("Search")) {
				// System.out.println("User searched something");
				String searchQuery = request.getParameter("query");
				request.setAttribute("books", dao.getBooksBySearch(searchQuery));
			} else if (category.equalsIgnoreCase("topRated")) {
				request.setAttribute("books", dao.getTopRated());
			} else {
				request.setAttribute("books", dao.getAllBooksByCategory(category));
			}
		}
		else if (action.equalsIgnoreCase("bookDetail")){
			forward = BOOK_DETAIL;
			int bookId = Integer.parseInt(request.getParameter("bookId"));
			// System.out.println("bookId = " + bookId);
			Book book = dao.getBookById(bookId);
			request.setAttribute("book", book);
			// System.out.println(book.toString());
		}
		else {
			forward = INSERT_OR_EDIT;
		}

		RequestDispatcher view = request.getRequestDispatcher(forward);
		view.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Book book = new Book();
		book.setAuthor(request.getParameter("author"));
		book.setCategory(request.getParameter("category"));
		book.setTitle(request.getParameter("title"));
		String inventory = request.getParameter("inventoryAmount");
		if (inventory == null || inventory.isEmpty()){
			book.setInventory(0);
		}
		else{
			int inv = Integer.parseInt(inventory);
			book.setInventory(inv);
		}
		String rating = request.getParameter("reviewRating");
		if (rating == null || rating.isEmpty()){
			// System.out.println("Review was null or empty");
			book.setReviewRating(0);
		}
		else{
			int rate = Integer.parseInt(rating);
			// System.out.println("Setting a Book Rating <===");
			book.setReviewRating(rate);
		}

		String priceFromForm = request.getParameter("price");
		// System.out.println("Price from form: " + priceFromForm);
		if(priceFromForm == null || priceFromForm.isEmpty()){
			// System.out.println("Price was null for Book Add");
			book.setPrice(0.0);
		} else {
			book.setPrice(Double.parseDouble(request.getParameter("price")));
			// System.out.println("Price was valid!");
		}
		
		book.setYearPublished(request.getParameter("publicationYear"));
		book.setPublisher(request.getParameter("publisher"));
		book.setPhoto(request.getParameter("photo"));
		
		String bookId = request.getParameter("bookId");
		if(bookId == null || bookId.isEmpty()){
			dao.addBook(book);
		}
		else
		{
			book.setBookId(Integer.parseInt(bookId));
			dao.updateBook(book);
		}

		// System.out.println("Book OBJ: " + book.toString());
		
		RequestDispatcher view = request.getRequestDispatcher(LIST_BOOK);
		request.setAttribute("books", dao.getAllBooks());
		view.forward(request, response);
	}

}
