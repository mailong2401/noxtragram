import 'package:flutter/material.dart';
class SimpleComboBox extends StatefulWidget {
  final List<String> items;
  final String? initialValue;
  final Function(String)? onItemSelected;

  const SimpleComboBox({
    Key? key,
    required this.items,
    this.initialValue,
    this.onItemSelected,
  }) : super(key: key);

  @override
  State<SimpleComboBox> createState() => _SimpleComboBoxState();
}

class _SimpleComboBoxState extends State<SimpleComboBox> {
  late String _selectedValue;

  @override
  void initState() {
    super.initState();
    _selectedValue = widget.initialValue ?? widget.items.first;
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        //border: Border.all(color: Colors.red),
        borderRadius: BorderRadius.circular(8),
      ),
      child: DropdownButton<String>(
        value: _selectedValue,
        icon: Icon(Icons.arrow_drop_down, color: Colors.white, size: 30,),
        underline: SizedBox(), // Ẩn gạch chân
        isExpanded: false,
        style: const TextStyle(color: Colors.white, fontSize: 17),
        dropdownColor: Colors.black,
        items: widget.items.map<DropdownMenuItem<String>>((String value) {
          return DropdownMenuItem<String>(
            value: value,
            child: Padding(
              padding: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              child: Text(value, style: const TextStyle(color: Colors.white,fontSize: 17)),
            ),
          );
        }).toList(),
        onChanged: (String? newValue) {
          if (newValue != null) {
            setState(() {
              _selectedValue = newValue;
            });
            // Gọi callback khi chọn item
            widget.onItemSelected?.call(newValue);
          }
        },
      ),
    );
  }
}
// Hàm xây dựng icon bên trái tùy theo index
Widget _buildLeadingIcon(int index) {
  // Có thể thay đổi icon theo loại chat
  if (index % 4 == 0) {
    return CircleAvatar(
      backgroundColor: Colors.blue,
      child: Icon(Icons.person, color: Colors.white),
    );
  } else if (index % 4 == 1) {
    return CircleAvatar(
      backgroundColor: Colors.green,
      child: Icon(Icons.group, color: Colors.white),
    );
  } else if (index % 4 == 2) {
    return CircleAvatar(
      backgroundColor: Colors.orange,
      child: Icon(Icons.business, color: Colors.white),
    );
  } else {
    return CircleAvatar(
      backgroundColor: Colors.purple,
      child: Icon(Icons.chat, color: Colors.white),
    );
  }
}
// Widget _buildLeadingIcon(int index) {
//   // Nếu có URL ảnh thì dùng, không thì dùng avatar mặc định
//   return CircleAvatar(
//     backgroundImage: NetworkImage('https://picsum.photos/100?random=$index'),
//     backgroundColor: Colors.grey[300],
//     child: Icon(Icons.person, color: Colors.white), // Fallback icon
//   );
// }