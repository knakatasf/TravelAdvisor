package hotelapp.handler;

import hotelapp.CONST;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Handles program arguments and stores in a hashMap.
 */
public class ArgumentHandler {
    private Map<String, String> argsMap;

    public ArgumentHandler() { argsMap = new HashMap<>(); }

    /**
     * Creates a hashMap containing program arguments; Validates each argument.
     * @param args to be included into the hashMap.
     * @throws IllegalArgumentException if any invalid argument found.
     */
    public void parseArguments(String[] args) throws IllegalArgumentException {
        if (args.length % 2 != 0)
            throw new IllegalArgumentException("Invalid command line arguments.");

        for (int i = 0; i < args.length; i += 2) {
            if (CONST.FLAG_SET.contains(args[i]))
                argsMap.put(args[i], args[i + 1]);
            else
                throw new IllegalArgumentException("Flag is invalid.");
        }

        try {
            argsMap.computeIfPresent(CONST.THREAD_FLAG, (k, v) -> {
                Integer.parseInt(v);
                return v;
            });
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Thread value has to be an integer.");
        }
    }

    /**
     * Gets an argument corresponding to a flag
     * @param flag to be gotten the argument
     * @return Optional argument variable
     */
    public Optional<String> getArgumentOf(String flag) {
        return Optional.ofNullable(argsMap.get(flag));
    }
}
