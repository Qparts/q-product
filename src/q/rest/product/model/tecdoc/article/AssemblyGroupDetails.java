package q.rest.product.model.tecdoc.article;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssemblyGroupDetails {
    private int assemblyGroupNodeId;
    private String assemblyGroupName;
    private String assemblyGroupType;
    private int parentNodeId;
    private int count;
    private int children;

    public int getAssemblyGroupNodeId() {
        return assemblyGroupNodeId;
    }

    public void setAssemblyGroupNodeId(int assemblyGroupNodeId) {
        this.assemblyGroupNodeId = assemblyGroupNodeId;
    }

    public String getAssemblyGroupName() {
        return assemblyGroupName;
    }

    public void setAssemblyGroupName(String assemblyGroupName) {
        this.assemblyGroupName = assemblyGroupName;
    }

    public String getAssemblyGroupType() {
        return assemblyGroupType;
    }

    public void setAssemblyGroupType(String assemblyGroupType) {
        this.assemblyGroupType = assemblyGroupType;
    }

    public int getParentNodeId() {
        return parentNodeId;
    }

    public void setParentNodeId(int parentNodeId) {
        this.parentNodeId = parentNodeId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getChildren() {
        return children;
    }

    public void setChildren(int children) {
        this.children = children;
    }
}
