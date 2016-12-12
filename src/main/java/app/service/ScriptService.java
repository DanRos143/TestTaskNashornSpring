package app.service;

import app.script.Script;

import javax.script.CompiledScript;
import javax.script.ScriptException;
import java.util.Collection;

/**
 * @author danros
 * @see javax.script.CompiledScript
 * @see app.script.Script
 *
 * Interface which contains service layer methods to manage Script class
 * includes methods to retrieve Script object, save Script object, get CompiledScript objects from plain/text string,
 * get collection of scripts or submit async task which evaluates script
 */

public interface ScriptService {
    /**
     * retrieves single Script object from Map
     * @param id An <code>Integer<code/> that is used as key
     * @return <code>Script<code/> if keySet contains key, else <code>null</code>
     */
    Script getScript(Integer id);

    /**
     * compiles script before evaluation
     * @param body A text/plain <code>String<code/> script source
     * @return <code>CompiledScript<code/>
     * @throws ScriptException if body is not a valid js script
     */
    CompiledScript compile(String body) throws ScriptException;

    /**
     * retrieves collection of map values
     * @return <code>Collection<Script><code/>
     */
    Collection<Script> getScripts();

    /**
     * invokes submit() method of executor to run async task
     * @param script Script object to be processed
     */
    void submitAsync(Script script);
    void saveScript(Script script);
}
