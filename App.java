import spark.Spark;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class App
{
	private static Gson GSON = new GsonBuilder().create();

	public static void main(String[] args)
	{
		Spark.get("/hello", (request, response) -> "Hello World!");

		Spark.get("/user/:id", (request, response) -> {
			Integer id = Integer.parseInt(request.params("id"));
			return GSON.toJson(User.get(id));
		});

		Spark.post("/user", (request, response) -> {
			User toStore = null;
			try {
				toStore = GSON.fromJson(request.body(), User.class);
			} catch(JsonSyntaxException e) {
				response.status(400);
				return "INVALID JSON";
			}

			if(toStore.getId() != null) {
				response.status(400);
				return "ID PROVIDED DURING CREATE";
			} else {
				User.store(toStore);
				return GSON.toJson(toStore);
			}
		});

		Spark.put("/user/:id", (request, response) -> {
			if(User.get(Integer.parseInt(request.params("id"))) == null) {
				response.status(404);
				return "NOT_FOUND";
			} else {
				User toStore = null;
				try {
					toStore = GSON.fromJson(request.body(), User.class);
				} catch(JsonSyntaxException e) {
					response.status(400);
					return "INVALID JSON";
				}
				User.store(toStore);
				return GSON.toJson(toStore);
			}
		});

		Spark.delete("/user/:id", (request, response) -> {
			User user = User.get(Integer.parseInt(request.params("id")));
			if(user == null) {
				response.status(404);
				return "NOT_FOUND";
			} else {
				User.delete(user);
				return "USER DELETED";
			}
		});

		Spark.before((request, response) -> {
			String method = request.requestMethod();
			if(method.equals("POST") || method.equals("PUT") || method.equals("DELETE")) {
				String authentication = request.headers("Authentication");
				if(!"PASSWORD".equals(authentication)) {
					Spark.halt(401, "User Unauthorized");
				}
			}
		});

		Spark.options("/*", (request, response) -> {
			String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
			if(accessControlRequestHeaders != null) {
				response.header("Access-Control-Request-Headers", accessControlRequestHeaders);
			}

			String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
			if(accessControlRequestHeaders != null) {
				response.header("Access-Control-Request-Method", accessControlRequestMethod);
			}
			return "OK";
		});

		Spark.before((request, response) -> {
			response.header("Access-Control-Allow-Origin", "*");
		});
	}
}