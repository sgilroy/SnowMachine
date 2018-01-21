package snowMachine;

class SnowflakeNodeInstance {
    private final int symmetricalDivisions;
    private final float branchOffset;
    private final float branchLengthScale;
    private final float branchWidthScale;
    private final float spreadAngle;

    SnowflakeNodeInstance(int symmetricalDivisions, float branchOffset, float branchLengthScale, float branchWidthScale, float spreadAngle) {

        this.symmetricalDivisions = symmetricalDivisions;
        this.branchOffset = branchOffset;
        this.branchLengthScale = branchLengthScale;
        this.branchWidthScale = branchWidthScale;
        this.spreadAngle = spreadAngle;
    }

    int getSymmetricalDivisions() {
        return symmetricalDivisions;
    }

    float getBranchOffset() {
        return branchOffset;
    }

    float getBranchLengthScale() {
        return branchLengthScale;
    }

    float getBranchWidthScale() {
        return branchWidthScale;
    }

    float getSpreadAngle() {
        return spreadAngle;
    }
}
