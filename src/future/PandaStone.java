package no1gaf;

class PandaStone {
    /*
     * Thanks to Panda4994 for his Redstone update rewrite code, which I used as an example to write this.
     * These variables and everything that uses them are based on his code.
     * https://gist.github.com/Panda4994/70ed6d39c89396570e062e4404a8d518
     */
    private List<BlockPos> turnOn = new ArrayList<>();
    private List<BlockPos> turnOff = new ArrayList<>();
    private final Set<BlockPos> updatedWires = new LinkedHashSet<>();

    private static final Direction[] orderedFacings;
    static {
        ArrayList<Direction> temp = new ArrayList<>();
        Direction.Type.VERTICAL.forEach(temp::add);
        Direction.Type.HORIZONTAL.forEach(temp::add);
        orderedFacings = temp.toArray(new Direction[0]);
    }

    /* End Panda variables */
    /* Begin Panda power logic */

    private void updateSurroundingRedstone(World world, BlockPos pos) {
        calculateCurrentChanges(world, pos);

        Set<BlockPos> blocksNeedingUpdate = new LinkedHashSet<>();
        for(BlockPos updatePos : updatedWires) addBlocksNeedingUpdate(world, updatePos, blocksNeedingUpdate);

        Iterator<BlockPos> it = new LinkedList<>(updatedWires).descendingIterator();
        while(it.hasNext()) addAllSurroundingBlocks(it.next(), blocksNeedingUpdate);

        blocksNeedingUpdate.removeAll(updatedWires);
        updatedWires.clear();

        for(BlockPos updatePos : blocksNeedingUpdate)
            world.updateNeighbor(updatePos, this, pos);
    }

    private void calculateCurrentChanges(World world, BlockPos pos) {
        if(isWire(world.getBlockState(pos))) turnOff.add(pos);

        while(!turnOff.isEmpty()) {
            BlockPos updatePos = turnOff.remove(0);
            BlockState state = world.getBlockState(updatePos);

            int oldPower = state.get(POWER);
            this.wiresGivePower = false;
            int blockPower = world.getReceivedRedstonePower(pos);
            this.wiresGivePower = true;
            int wirePower = getSurroundingWirePower(world, pos)-1;
            int newPower = Math.max(blockPower, wirePower);

            if(newPower > oldPower)
                setWireState(world, updatePos, state, newPower);
            else if(newPower < oldPower) {
                if(blockPower > 0 && !turnOn.contains(updatePos)) turnOn.add(updatePos);
                setWireState(world, updatePos, state, 0);
            }
        }

        while(!turnOn.isEmpty()) {
            BlockPos updatePos = turnOn.remove(0);
            BlockState state = world.getBlockState(updatePos);

            int oldPower = state.get(POWER);
            this.wiresGivePower = false;
            int blockPower = world.getReceivedRedstonePower(pos);
            this.wiresGivePower = true;
            int wirePower = getSurroundingWirePower(world, pos)-1;
            int newPower = Math.max(blockPower, wirePower);

            if(newPower > oldPower)
                setWireState(world, updatePos, state, newPower);
            checkSurroundingWires(world, updatePos);
        }

        turnOn.clear();
        turnOff.clear();
    }

    private void addWireToList(World world, BlockPos pos, int otherPower) {
        BlockState state = world.getBlockState(pos);
        if(isWire(state)) {
            int power = state.get(POWER);
            if(power < otherPower-1 && !turnOn.contains(pos)) turnOn.add(pos);
            if(power > otherPower && !turnOff.contains(pos)) turnOff.add(pos);
        }
    }

    private void checkSurroundingWires(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        int ownPower = 0;
        if(isWire(state)) ownPower = state.get(POWER);

        for(Direction dir : Direction.Type.HORIZONTAL)
            addWireToList(world, pos.offset(dir), ownPower);
        for(Direction dir : Direction.Type.VERTICAL) {
            BlockPos offsetPos = pos.offset(dir);
            boolean solidBlock = world.getBlockState(offsetPos).isSimpleFullBlock(world, offsetPos);
            for(Direction hor : Direction.Type.HORIZONTAL) {
                BlockPos horOffPos = offsetPos.offset(hor);
                boolean horSolidBlock = world.getBlockState(horOffPos).isSimpleFullBlock(world, horOffPos);
                if((dir == Direction.UP && !solidBlock) ||
                   (dir == Direction.DOWN && solidBlock && horSolidBlock))
                    addWireToList(world, horOffPos, ownPower);
            }
        }
    }

    private int getSurroundingWirePower(World world, BlockPos pos) {
        int wirePower = 0;
        for(Direction dir : Direction.Type.HORIZONTAL) {
            BlockPos offsetPos = pos.offset(dir);
            wirePower = getMaxCurrentStrength(world, offsetPos, wirePower);

            if(world.getBlockState(offsetPos).isSimpleFullBlock(world, offsetPos)) {
                if(!world.getBlockState(pos.up()).isSimpleFullBlock(world, pos.up()))
                    wirePower = getMaxCurrentStrength(world, offsetPos.up(), wirePower);
            } else
                wirePower = getMaxCurrentStrength(world, offsetPos.down(), wirePower);
        }
        return wirePower;
    }

    private void addBlocksNeedingUpdate(World world, BlockPos pos, Set<BlockPos> set) {
        List<Direction> connectedSides = getPowerReceivableDirections(world, pos);

        for(Direction dir : orderedFacings) {
            BlockPos offsetPos = pos.offset(dir);
            if(connectedSides.contains(dir.getOpposite()) || dir == Direction.DOWN ||
               (dir.getAxis().isHorizontal() && shouldConnect(world.getBlockState(pos), world.getBlockState(offsetPos), dir)))
                if(canBlockBePoweredFromSide(world.getBlockState(offsetPos), dir, true)) set.add(offsetPos);
        }

        for(Direction dir : orderedFacings) {
            BlockPos offsetPos = pos.offset(dir);
            if(connectedSides.contains(dir.getOpposite()) || dir == Direction.DOWN)
                if(world.getBlockState(offsetPos).isSimpleFullBlock(world, offsetPos))
                    for(Direction dir2 : orderedFacings)
                        if(canBlockBePoweredFromSide(world.getBlockState(offsetPos.offset(dir2)), dir, false))
                            set.add(offsetPos.offset(dir2));
        }
    }

    private boolean canBlockBePoweredFromSide(BlockState state, Direction side, boolean isWire) {
        if(state.getBlock() instanceof PistonBlock && state.get(PistonBlock.FACING) == side.getOpposite()) return false;
        if(state.getBlock() instanceof AbstractRedstoneGateBlock && state.get(Properties.FACING_HORIZONTAL) != side.getOpposite()) {
            if(isWire && state.getBlock() instanceof ComparatorBlock && state.get(ComparatorBlock.FACING).getAxis() != side.getAxis())
                return side.getAxis().isHorizontal();
            return false;
        }
        if(state.getBlock() instanceof WallRedstoneTorchBlock)
            if(isWire || state.get(WallRedstoneTorchBlock.FACING) != side) return false;
        if(state.getBlock() instanceof RedstoneTorchBlock) return isWire;
        return true;
    }

    private int getMaxCurrentStrength(World world, BlockPos pos, int strength) {
        if(!isWire(world.getBlockState(pos))) return strength;
        return Math.max(world.getBlockState(pos).get(POWER), strength);
    }

    private void setWireState(World world, BlockPos pos, BlockState state, int power) {
        world.setBlockState(pos, state.with(POWER, power), 2);
        updatedWires.add(pos);
    }

    private void addAllSurroundingBlocks(BlockPos pos, Set<BlockPos> set) {
        for(Direction dir : orderedFacings) set.add(pos.offset(dir));
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState state2, boolean flag) {
        if(!world.isClient) {
            this.updateSurroundingRedstone(world, pos);
            for(Direction dir : orderedFacings) world.updateNeighbor(pos.offset(dir), this, pos);
        }
    }

    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState state2, boolean flag) {
        if(!world.isClient) {
            this.updateSurroundingRedstone(world, pos);
            for(Direction dir : orderedFacings) world.updateNeighbor(pos.offset(dir), this, pos);
        }
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos pos2, boolean flag) {
        if(!world.isClient) {
            if(state.canPlaceAt(world, pos)) {
                this.updateSurroundingRedstone(world, pos);
                for(Direction dir : orderedFacings) world.updateNeighbor(pos.offset(dir), this, pos);
            } else {
                dropStacks(state, world, pos);
                world.clearBlockState(pos, false);
            }
        }
    }

    /* End Panda power logic */
}
