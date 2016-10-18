package com.dnk.smart.kit;

import com.google.gson.*;

public class GsonKit {

	private static final GsonBuilder BUILDER = new GsonBuilder();
	private static final Gson GSON = BUILDER.create();

	public static JsonObject getJson(String json) {
//		return GSON.fromJson(json, JsonObject.class);
		return new JsonParser().parse(json).getAsJsonObject();
	}

	public static String toJson(String key, String value) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(key, value);
		return jsonObject.toString();
	}

	public static String toJson(Object object) {
		return GSON.toJson(object);
	}

	public static boolean isEmpty(JsonObject jsonObject) {
		return jsonObject.entrySet().size() == 0;
	}

	private static JsonElement get(JsonObject jsonObject, String key) {
		return jsonObject.get(key);
	}

	public static int getInt(JsonObject jsonObject, String key) {
		return get(jsonObject, key).getAsInt();
	}

	public static boolean getBoolean(JsonObject jsonObject, String key) {
		return get(jsonObject, key).getAsBoolean();
	}

	public static String getString(JsonObject jsonObject, String key) {
		return get(jsonObject, key).getAsString();
	}

	public static String getString(String json, String key) {
		return getString(getJson(json), key);
	}

	public static void main(String[] args) {
		String a = "{\"a\":1}";
		String b = "{b:2}";
		System.out.println(getJson(a));
		System.out.println(getJson(b));

		System.out.println(toJson(1));
		System.out.println(toJson(new int[]{1, 2, 3}));

		System.out.println(toJson("a", "2"));
	}

	/*public static String toJson(String str) {
		Gson gson = BUILDER.create();
		gson.
	}*/
}
