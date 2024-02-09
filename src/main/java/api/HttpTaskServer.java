package api;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import exception.*;
import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class HttpTaskServer {
    private final static String PATH = "/tasks";
    private final int PORT = 8080;
    private final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final Gson gson;
    private final HttpServer httpServer;

    public HttpTaskServer() throws IOException {
        gson = Managers.getCustomGson();
        TaskManager taskManager = Managers.getDefault();

        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext(PATH, new TaskHandler(taskManager));
    }

    private class TaskHandler implements HttpHandler {

        private final TaskManager taskManager;

        private TaskHandler(TaskManager taskManager) {
            this.taskManager = taskManager;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Endpoint endpoint = Endpoint.getEndpoint(exchange);
            System.out.println("API: Получен запрос: " + endpoint);

            String requestBody = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
            Headers responseHeaders = exchange.getResponseHeaders();
            String responseString = "";
            int responseCode;

            Map<String, String> params = new HashMap<>();
            if (exchange.getRequestURI().getQuery() != null) {
                String[] paramsSplit = exchange.getRequestURI().getQuery().split("&");
                Arrays.stream(paramsSplit).forEach(p -> {
                    String[] split = p.split("=");
                    params.put(split[0], split[1]);
                });
            }

            Task task;
            switch (endpoint) {
                case GET_TASKS:
                    responseString = gson.toJson(taskManager.getTasks());
                    responseHeaders.add("Content-Type", "application/json");
                    responseCode = 200;
                    break;
                case GET_TASK:
                    try {
                        responseString = gson.toJson(taskManager.getTask(Long.parseLong(params.get("id"))));
                        if (responseString.equals("null")) {
                            responseCode = 404;
                        } else {
                            responseHeaders.add("Content-Type", "application/json");
                            responseCode = 200;
                        }
                    } catch (NumberFormatException e) {
                        responseCode = 400;
                    }
                    break;
                case POST_TASK:
                    task = gson.fromJson(requestBody, Task.class);
                    try {
                        responseString = String.valueOf(taskManager.createTask(task));
                        responseHeaders.add("Content-Type", "application/json");
                        responseCode = 201;
                    } catch (ManagerTaskBadInputException | ManagerTaskTimeIntersectionException e) {
                        responseCode = 400;
                    } catch (ManagerTaskAlreadyExistException e) {
                        taskManager.updateTask(task);
                        responseCode = 204;
                    }
                    break;
                case DELETE_TASK:
                    try {
                        taskManager.removeTask(Long.parseLong(params.get("id")));
                        responseCode = 204;
                    } catch (NumberFormatException e) {
                        responseCode = 400;
                    }
                    break;
                case DELETE_TASKS:
                    taskManager.clearTasks();
                    responseCode = 204;
                    break;
                case GET_SUBTASKS:
                    responseString = gson.toJson(taskManager.getEpicSubtasks());
                    responseHeaders.add("Content-Type", "application/json");
                    responseCode = 200;
                    break;
                case GET_SUBTASK:
                    try {
                        responseString = gson.toJson(taskManager.getSubtask(Long.parseLong(params.get("id"))));
                        if (responseString.equals("null")) {
                            responseCode = 404;
                        } else {
                            responseHeaders.add("Content-Type", "application/json");
                            responseCode = 200;
                        }
                    } catch (NumberFormatException e) {
                        responseCode = 400;
                    }
                    break;
                case POST_SUBTASK:
                    task = gson.fromJson(requestBody, Subtask.class);
                    try {
                        responseString = String.valueOf(taskManager.createSubtask((Subtask) task));
                        responseHeaders.add("Content-Type", "application/json");
                        responseCode = 201;
                    } catch (ManagerTaskBadInputException | ManagerTaskTimeIntersectionException e) {
                        responseCode = 400;
                    } catch (ManagerTaskAlreadyExistException e) {
                        taskManager.updateSubtask((Subtask) task);
                        responseCode = 204;
                    }
                    break;
                case DELETE_SUBTASK:
                    try {
                        taskManager.removeSubtask(Long.parseLong(params.get("id")));
                        responseCode = 204;
                    } catch (NumberFormatException e) {
                        responseCode = 400;
                    }
                    break;
                case DELETE_SUBTASKS:
                    taskManager.clearSubtasks();
                    responseCode = 204;
                    break;
                case GET_EPICS:
                    responseString = gson.toJson(taskManager.getEpics());
                    responseHeaders.add("Content-Type", "application/json");
                    responseCode = 200;
                    break;
                case GET_EPIC:
                    try {
                        responseString = gson.toJson(taskManager.getEpic(Long.parseLong(params.get("id"))));
                        if (responseString.equals("null")) {
                            responseCode = 404;
                        } else {
                            responseHeaders.add("Content-Type", "application/json");
                            responseCode = 200;
                        }
                    } catch (NumberFormatException e) {
                        responseCode = 400;
                    }
                    break;
                case POST_EPIC:
                    task = gson.fromJson(requestBody, Epic.class);
                    try {
                        responseString = String.valueOf(taskManager.createEpic((Epic) task));
                        responseHeaders.add("Content-Type", "application/json");
                        responseCode = 201;
                    } catch (ManagerTaskBadInputException | ManagerTaskTimeIntersectionException e) {
                        responseCode = 400;
                    } catch (ManagerTaskAlreadyExistException e) {
                        taskManager.updateEpic((Epic) task);
                        responseCode = 204;
                    }
                    break;
                case DELETE_EPIC:
                    try {
                        taskManager.removeEpic(Long.parseLong(params.get("id")));
                        responseCode = 204;
                    } catch (NumberFormatException e) {
                        responseCode = 400;
                    }
                    break;
                case DELETE_EPICS:
                    taskManager.clearEpics();
                    responseCode = 204;
                    break;
                case GET_EPIC_SUBTASKS:
                    try {
                        List<Subtask> ids = taskManager.getEpicSubtasks(Long.parseLong(params.get("id")));
                        responseString = gson.toJson(ids);
                        responseHeaders.add("Content-Type", "application/json");
                        responseCode = 200;
                    } catch (ManagerTaskNotFoundException e) {
                        responseCode = 404;
                    }
                    break;
                case GET_HISTORY:
                    responseString = gson.toJson(taskManager.getHistory());
                    responseHeaders.add("Content-Type", "application/json");
                    responseCode = 200;
                    break;
                case GET_PRIORITIZED_TASKS:
                    responseString = gson.toJson(taskManager.getPrioritizedTasks());
                    responseHeaders.add("Content-Type", "application/json");
                    responseCode = 200;
                    break;
                default:
                    responseString = "";
                    responseCode = 404;
            }
            writeResponse(exchange, responseString, responseCode);
        }

        private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
            if (responseString.isEmpty()) {
                exchange.sendResponseHeaders(responseCode, 0);

            } else {
                byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
                exchange.sendResponseHeaders(responseCode, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
            exchange.close();
        }
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(1);
    }

    public enum Endpoint {
        GET_TASKS(RequestMethod.GET, PATH + "/task/"),
        GET_TASK(RequestMethod.GET, PATH + "/task/", "id"),
        POST_TASK(RequestMethod.POST, PATH + "/task/"),
        DELETE_TASK(RequestMethod.DELETE, PATH + "/task/", "id"),
        DELETE_TASKS(RequestMethod.DELETE, PATH + "/task/"),

        GET_SUBTASKS(RequestMethod.GET, PATH + "/subtask/"),
        GET_SUBTASK(RequestMethod.GET, PATH + "/subtask/", "id"),
        POST_SUBTASK(RequestMethod.POST, PATH + "/subtask/"),
        DELETE_SUBTASK(RequestMethod.DELETE, PATH + "/subtask/", "id"),
        DELETE_SUBTASKS(RequestMethod.DELETE, PATH + "/subtask/"),

        GET_EPICS(RequestMethod.GET, PATH + "/epic/"),
        GET_EPIC(RequestMethod.GET, PATH + "/epic/", "id"),
        POST_EPIC(RequestMethod.POST, PATH + "/epic/"),
        DELETE_EPIC(RequestMethod.DELETE, PATH + "/epic/", "id"),
        DELETE_EPICS(RequestMethod.DELETE, PATH + "/epic/"),
        GET_EPIC_SUBTASKS(RequestMethod.GET, PATH + "/subtask/epic/", "id"),

        GET_HISTORY(RequestMethod.GET, PATH + "/history/"),
        GET_PRIORITIZED_TASKS(RequestMethod.GET, PATH),

        UNKNOWN();

        public static Endpoint getEndpoint(HttpExchange exchange) {
            final String requestMethod = exchange.getRequestMethod();
            final String requestPath = exchange.getRequestURI().getPath();
            final String requestParams = exchange.getRequestURI().getQuery();

            List<Endpoint> endpoints = Arrays.stream(Endpoint.values())
                    .filter(e -> e != UNKNOWN)
                    .filter(endpoint -> endpoint.getMethod() == RequestMethod.valueOf(requestMethod))
                    .filter(endpoint -> endpoint.getPath().equals(requestPath))
                    .filter(endpoint -> {
                        if (requestParams == null && endpoint.getParams().size() == 0) {
                            return true;
                        } else if (requestParams == null) {
                            return false;
                        } else if (endpoint.getParams().size() == 0) {
                            return false;
                        } else {
                            String[] splitRequestParams = requestParams.split("&");
                            Optional<String> mismatchParam = Arrays.stream(splitRequestParams)
                                    .map(p -> p.substring(0, p.indexOf("=")))
                                    .filter(p -> !endpoint.getParams().contains(p)).findFirst();
                            return mismatchParam.isEmpty();
                        }
                    }).collect(Collectors.toList());
            if (endpoints.size() == 0) {
                return UNKNOWN;
            } else if (endpoints.size() == 1) {
                return endpoints.get(0);
            } else {
                throw new RuntimeException("Конфликт конечных точек");
            }
        }

        private final String path;
        private final RequestMethod method;
        private final List<String> params;

        Endpoint() {
            this.method = null;
            this.path = null;
            this.params = null;
        }

        Endpoint(RequestMethod method, String path, String... params) {
            this.method = method;
            this.path = path;
            this.params = List.of(params);
        }

        public String getPath() {
            return path;
        }

        public String getPath(Map<String, String> params) {
            if (params.size() > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(path).append("?");
                params.forEach((key, value) -> sb.append(key).append("=").append(value));
                return sb.toString();
            } else {
                return path;
            }
        }

        public RequestMethod getMethod() {
            return method;
        }

        public List<String> getParams() {
            return params;
        }

        private enum RequestMethod {
            GET,
            POST,
            DELETE
        }
    }
}
