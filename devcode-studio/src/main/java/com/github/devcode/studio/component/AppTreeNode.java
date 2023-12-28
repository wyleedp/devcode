package com.github.devcode.studio.component;

public class AppTreeNode {

	public static final String DEV_TOOLS_NODE = "DEV_TOOLS_NODE";
	public static final String JAVA_PROPERTIES_NODE = "JAVA_PROPERTIES_NODE";
	public static final String OS_ENV_NODE = "OS_ENV_NODE";
	public static final String UUID_NODE = "UUID_NODE";
	public static final String RANDOM_STRING_NODE = "RANDOM_STRING_NODE";
	
	private String nodeId;
	private String nodeName;
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
	public AppTreeNode(String nodeId, String nodeName) {
		this.nodeId = nodeId;
		this.nodeName = nodeName;
	}
	
	@Override
	public String toString() {
		return getNodeName();
	}
	
}
