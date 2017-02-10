public class App {

	private static Gson GSON = new GsonBuilder().create();

	public static void main(String[] args) {
		Spark.get("/hello", (request, response) -> "Hello World");
		Spark.get("/user/:id", (request, response) -> {
			Integer id = Integer.parseInt(erquest.params)
		})
	}
}