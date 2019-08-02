/**  UnsupportedTypeException.java
 * 
 * Copyright 2019 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.datakurator.ffdq.runner;

/**
 * Exception intended to flag types asserted in input CSV that are not supported
 * by the org.datakurator.ffdq.model 
 * 
 * @author Paul J. Morris
 */
public class UnsupportedTypeException extends Exception {

	public UnsupportedTypeException(String message) {
		super(message);
	}

}
