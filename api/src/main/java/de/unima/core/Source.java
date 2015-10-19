package de.unima.core;

import java.io.InputStream;

/**
 * Abstraction of the source of a process
 * @author Christian
 *
 */
public interface Source {
    /**
     * Returns content as {@code InputStream}
     * @return content of this source
     */
    InputStream getContent();
}
