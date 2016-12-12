package app.service;

import app.script.Script;

import javax.script.ScriptException;
import java.util.Collection;

/**
 * This interface provides operations to manipulate Script class objects.
 * Includes methods to retrieve Script object, save Script object, get CompiledScript objects from plain/text string,
 * get collection of scripts, submit asynchronous script evaluation
 * @author danros
 * @see javax.script.CompiledScript
 * @see app.script.Script
 *
 */

public interface ScriptService {
    /**
     * retrieves single Script object from Map
     * @param id An <code>Integer</code> key
     * @return <code>Script</code> if keySet contains key, else <code>null</code>
     */
    Script getScript(Integer id);

    /**
     *
     * @param body source javascript code represented by <code>String</code>
     * @return saved <code>Script</code> object if source is valid, else ScriptException is thrown
     * @throws ScriptException if source is invalid
     */
    Script compileAndSave(String body) throws ScriptException;

    /**
     * Obtain the Collection of Script
     * @return <code>Collection&lt;Script&gt;</code>
     */
    Collection<Script> getScripts();

    /**
     * invokes submit() method of executor to run async task
     * @param script Script object to be processed
     */
    void submitAsync(Script script);

}
