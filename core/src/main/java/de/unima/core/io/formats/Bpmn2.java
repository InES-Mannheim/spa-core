package de.unima.core.io.formats;

import java.util.Collection;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

import de.unima.core.io.Format;

public class Bpmn2 implements Format<BpmnModelInstance, BpmnModelInstance> {
	
	private final String individualNameSpace;

	public Bpmn2(String individualNameSpace) {
		this.individualNameSpace = individualNameSpace;
	}

	@Override
	public Writes<? extends Collection<BpmnModelInstance>> write() {
		return null;
	}

	@Override
	public Serialize<BpmnModelInstance> serialize() {
		return null;
	}

	@Override
	public Reads<BpmnModelInstance> read() {
		return new BPMN2Reads(individualNameSpace)::convertToRdf;
	}

	@Override
	public Deserialize<BpmnModelInstance> deserialize() {
		return Bpmn::readModelFromStream;
	}

}
