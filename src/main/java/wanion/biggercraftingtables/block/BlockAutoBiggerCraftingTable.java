package wanion.biggercraftingtables.block;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import wanion.biggercraftingtables.BiggerCraftingTables;
import wanion.biggercraftingtables.Reference;
import wanion.biggercraftingtables.block.big.TileEntityAutoBigCraftingTable;
import wanion.biggercraftingtables.block.huge.TileEntityAutoHugeCraftingTable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

import static wanion.biggercraftingtables.Reference.MOD_ID;

public final class BlockAutoBiggerCraftingTable extends BlockContainer
{
	public static final BlockAutoBiggerCraftingTable instance = new BlockAutoBiggerCraftingTable();
	@SideOnly(Side.CLIENT)
	private static IIcon[][] textures;

	private BlockAutoBiggerCraftingTable()
	{
		super(Material.wood);
		setHardness(2.5F).setStepSound(soundTypeWood).setCreativeTab(BiggerCraftingTables.creativeTabs);
	}

	@Override
	public TileEntity createNewTileEntity(final World world, final int metadata)
	{
		switch (metadata) {
			case 0:
				return new TileEntityAutoBigCraftingTable();
			case 1:
				return new TileEntityAutoHugeCraftingTable();
			default:
				return null;
		}
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(@Nonnull final IIconRegister iIconRegister)
	{
		textures = new IIcon[2][3];
		for (int i = 0; i < Reference.TYPES.size(); i++) {
			final String type = Reference.TYPES.get(i);
			textures[i][0] = iIconRegister.registerIcon(MOD_ID + ":Auto" + type + "CraftingTableTop");
			textures[i][1] = iIconRegister.registerIcon(MOD_ID + ":AutoBiggerCraftingTablesBottom");
			textures[i][2] = iIconRegister.registerIcon(MOD_ID + ":AutoBiggerCraftingTablesSides");
		}
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(final int side, int metadata)
	{
		if (metadata < 0 || metadata > 1)
			metadata = 0;
		switch (side) {
			case 0:
				return textures[metadata][1];
			case 1:
				return textures[metadata][0];
			default:
				return textures[metadata][2];
		}
	}

	@SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(final Item block, final CreativeTabs creativeTabs, final List list)
	{
		for (int i = 0; i < 2; i++)
			list.add(new ItemStack(block, 1, i));
	}

	@Override
	public boolean onBlockActivated(final World world, final int x, final int y, final int z, final EntityPlayer entityPlayer, final int side, final float hitX, final float hitY, final float hitZ)
	{
		if (!world.isRemote) {
			final TileEntity tileEntity = world.getTileEntity(x, y, z);
			if (tileEntity instanceof TileEntityAutoBigCraftingTable)
				FMLNetworkHandler.openGui(entityPlayer, BiggerCraftingTables.instance, BiggerCraftingTables.GUI_ID_AUTO_BIG_CRAFTING_TABLE, world, x, y, z);
			else if (tileEntity instanceof TileEntityAutoHugeCraftingTable)
				FMLNetworkHandler.openGui(entityPlayer, BiggerCraftingTables.instance, BiggerCraftingTables.GUI_ID_AUTO_HUGE_CRAFTING_TABLE, world, x, y, z);
			else
				return false;
		}
		return true;
	}

	@Override
	public int damageDropped(final int metadata)
	{
		return metadata;
	}

	@Override
	public final void breakBlock(final World world, final int x, final int y, final int z, final Block block, final int metadata)
	{
		final TileEntityAutoBiggerCraftingTable tileEntityBiggerCraftingTable = (TileEntityAutoBiggerCraftingTable) world.getTileEntity(x, y, z);
		if (tileEntityBiggerCraftingTable != null) {
			final ItemStack droppedStack = new ItemStack(block, 1, metadata);
			final NBTTagCompound nbtTagCompound = tileEntityBiggerCraftingTable.writeCustomNBT(new NBTTagCompound());
			if (nbtTagCompound.getTagList("Contents", 10).tagCount() > 0)
				droppedStack.setTagCompound(nbtTagCompound);
			world.spawnEntityInWorld(new EntityItem(world, x + Reference.RANDOM.nextFloat() * 0.8F + 0.1F, y + Reference.RANDOM.nextFloat() * 0.8F + 0.1F, z + Reference.RANDOM.nextFloat() * 0.8F + 0.1F, droppedStack));
			world.func_147453_f(x, y, z, block);
		}
		world.removeTileEntity(x, y, z);
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
	{
		return null;
	}

	@Override
	public final void onBlockPlacedBy(final World world, final int x, final int y, final int z, final EntityLivingBase entity, final ItemStack itemStack)
	{
		final TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityAutoBiggerCraftingTable && itemStack.stackTagCompound != null)
			((TileEntityAutoBiggerCraftingTable) tileEntity).readCustomNBT(itemStack.stackTagCompound);
	}
}